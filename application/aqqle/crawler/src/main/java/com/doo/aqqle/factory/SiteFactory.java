package com.doo.aqqle.factory;

import com.doo.aqqle.component.Site;

public class SiteFactory {

    public Site getSite(SiteFactoryInterface siteFactoryInterface){
        return siteFactoryInterface.getSite();
    }
}
