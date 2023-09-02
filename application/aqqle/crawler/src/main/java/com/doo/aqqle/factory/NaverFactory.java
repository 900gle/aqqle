package com.doo.aqqle.factory;

import com.doo.aqqle.component.Naver;
import com.doo.aqqle.component.Site;

public class NaverFactory implements SiteFactoryInterface {
    @Override
    public Site getSite() {
        return new Naver();
    }
}
