package com.datguy.quadmis.middlemen;

import com.datguy.quadmis.data.QuadmisAttack;

public interface QuadmisAbstractEventHandler {
    void start();
    void stop();
    void setAutoLock(long millis);
    void cancelAutoLock();
    void setAttackTrigger(QuadmisAttack.QuadmisAttackByte attack);
    void handleReset();
}
