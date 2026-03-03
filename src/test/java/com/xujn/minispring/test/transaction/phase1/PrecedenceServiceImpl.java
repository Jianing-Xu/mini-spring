package com.xujn.minispring.test.transaction.phase1;

import com.xujn.minispring.context.annotation.Component;
import com.xujn.minispring.tx.annotation.Transactional;

@Component
@Transactional
public class PrecedenceServiceImpl implements PrecedenceService {

    @Override
    @Transactional
    public void work() {
    }
}
