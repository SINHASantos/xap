package org.openspaces.persistency.kafka;

import com.gigaspaces.internal.utils.concurrent.GSThreadFactory;
import com.gigaspaces.sync.*;
import com.gigaspaces.sync.serializable.*;
import com.gigaspaces.sync.serializable.AddIndexEndpointData;
import com.gigaspaces.sync.serializable.ConsolidationParticipantEndpointData;
import com.gigaspaces.sync.serializable.IntroduceTypeEndpointData;
import com.gigaspaces.sync.serializable.TransactionEndpointData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.*;
import org.openspaces.persistency.kafka.internal.*;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class KafkaSpaceSynchronizationEndpoint extends SpaceSynchronizationEndpoint {
    private final static Log logger = LogFactory.getLog(KafkaSpaceSynchronizationEndpoint.class);
    private final static long KAFKA_PRODUCE_TIMEOUT = 30;

    private final Producer<String, EndpointData> kafkaProducer;
    private final SpaceSynchronizationEndpointKafkaWriter spaceSynchronizationEndpointKafkaWriter;
    private final String topic;

    public KafkaSpaceSynchronizationEndpoint(SpaceSynchronizationEndpoint spaceSynchronizationEndpoint, Properties kafkaProps, String topic) {
        this.topic = topic;
        this.kafkaProducer = new KafkaProducer<>(initKafkaProducerProperties(kafkaProps));
        this.spaceSynchronizationEndpointKafkaWriter = new SpaceSynchronizationEndpointKafkaWriter(spaceSynchronizationEndpoint, initKafkaConsumerProperties(kafkaProps), topic);
    }

    private Properties initKafkaProducerProperties(Properties kafkaProps){
        Properties kafkaProducerProps = new Properties();
        Map<Object,Object> kafkaProducerPropsMap = new HashMap<>(kafkaProps);
        kafkaProducerPropsMap.putIfAbsent(ProducerConfig.ACKS_CONFIG, "1");
        kafkaProducerPropsMap.putIfAbsent(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");
        kafkaProducerPropsMap.putIfAbsent(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaEndpointDataSerializer.class.getName());
        kafkaProducerProps.putAll(kafkaProducerPropsMap);
        return kafkaProducerProps;
    }

    private Properties initKafkaConsumerProperties(Properties kafkaProps){
        Properties kafkaConsumerProps = new Properties();
        Map<Object,Object> kafkaConsumerPropsMap = new HashMap<>(kafkaProps);
        kafkaConsumerPropsMap.putIfAbsent(ConsumerConfig.GROUP_ID_CONFIG, topic + "-group");
        kafkaConsumerPropsMap.putIfAbsent(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        kafkaConsumerPropsMap.putIfAbsent(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringDeserializer");
        kafkaConsumerPropsMap.putIfAbsent(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaEndpointDataDeserializer.class.getName());
        kafkaConsumerPropsMap.putIfAbsent(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");
        kafkaConsumerProps.putAll(kafkaConsumerPropsMap);
        return kafkaConsumerProps;
    }

    public void start(){
        Thread thread = GSThreadFactory.daemon("kafka-consumer-" + topic).newThread(spaceSynchronizationEndpointKafkaWriter);
        thread.start();
    }

    @Override
    public void onTransactionConsolidationFailure(ConsolidationParticipantData participantData) {
        sendToKafka(new ProducerRecord<>(topic, new ConsolidationParticipantEndpointData(participantData, SpaceSyncEndpointMethod.onTransactionConsolidationFailure)));
    }

    @Override
    public void onTransactionSynchronization(TransactionData transactionData) {
        sendToKafka(new ProducerRecord<>(topic, new TransactionEndpointData(transactionData, SpaceSyncEndpointMethod.onTransactionSynchronization)));
    }

    @Override
    public void onOperationsBatchSynchronization(OperationsBatchData batchData) {
        sendToKafka(new ProducerRecord<>(topic, new OperationsBatchEndpointData(batchData, SpaceSyncEndpointMethod.onOperationsBatchSynchronization)));
    }

    @Override
    public void onAddIndex(com.gigaspaces.sync.AddIndexData addIndexData) {
        sendToKafka(new ProducerRecord<>(topic, new AddIndexEndpointData(addIndexData, SpaceSyncEndpointMethod.onAddIndex)));
    }

    @Override
    public void onIntroduceType(IntroduceTypeData introduceTypeData) {
        sendToKafka(new ProducerRecord<>(topic, new IntroduceTypeEndpointData(introduceTypeData, SpaceSyncEndpointMethod.onIntroduceType)));
    }

    private void sendToKafka(ProducerRecord<String, EndpointData> producerRecord){
        try {
            Future<RecordMetadata> future = kafkaProducer.send(producerRecord);
            future.get(KAFKA_PRODUCE_TIMEOUT, TimeUnit.SECONDS);
            if(logger.isDebugEnabled())
                logger.debug("Written message to Kafka: " + producerRecord);
        } catch (Exception e) {
            throw new SpaceKafkaException("Failed to write to kafka", e);
        }
    }

    public void close() {
        spaceSynchronizationEndpointKafkaWriter.close();
    }
}
