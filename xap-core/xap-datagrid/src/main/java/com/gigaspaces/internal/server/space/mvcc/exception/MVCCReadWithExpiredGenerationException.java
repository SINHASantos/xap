package com.gigaspaces.internal.server.space.mvcc.exception;

import com.j_spaces.core.cache.mvcc.MVCCEntryHolder;

/**
 * @author Davyd Savitskyi
 * @since 16.5.0
 */
public class MVCCReadWithExpiredGenerationException extends MVCCGenerationInternalRuntimeException {
    private static final long serialVersionUID = -367273521283968171L;

    public MVCCReadWithExpiredGenerationException(long providedGenerationState, long oldestConsistentGeneration) {
        super(String.format("Generation [%s] is not consistent (is less than oldest consistent generation: [%d])", providedGenerationState, oldestConsistentGeneration));
    }
}
