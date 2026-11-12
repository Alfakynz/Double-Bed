package com.alfakynz.doublebed;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class DoubleBed {

    public DoubleBed(IEventBus eventBus) {

        CommonClass.init();
    }
}