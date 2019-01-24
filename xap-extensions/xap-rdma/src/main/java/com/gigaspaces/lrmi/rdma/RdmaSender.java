package com.gigaspaces.lrmi.rdma;

import com.ibm.disni.util.DiSNILogger;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;

public class RdmaSender implements Runnable {

    private final ArrayBlockingQueue<RdmaMsg> writeRequests;
    private final RdmaResourceManager resourceManager;

    public RdmaSender(RdmaResourceManager resourceManager,
                      ArrayBlockingQueue<RdmaMsg> writeRequests) {
        this.writeRequests = writeRequests;
        this.resourceManager = resourceManager;
    }

    @Override
    public void run() {
        while (true) {
            try {
                RdmaMsg rdmaMsg = writeRequests.take();
                RdmaResource resource = resourceManager.waitForFreeResource();
                try {
                    DiSNILogger.getLogger().info("writing to client buffer");
                    resource.serialize(rdmaMsg.getId(), rdmaMsg.getRequest());
                    resource.getPostSend().execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
