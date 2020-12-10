package org.dishi.service.impl;

import org.dishi.dao.SiteMapper;
import org.dishi.entity.Site;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SiteService {
    @Autowired
    private SiteMapper siteMapper;

    @Transactional
    public int addSite(Site site) throws DuplicateKeyException {
        return siteMapper.addSite(site);
    }

    @Transactional
    public int deleteSiteById(Integer id){
        return siteMapper.deleteSiteById(id);
    }

    public List<Site> querySiteById(Integer uid){
        return siteMapper.querySiteById(uid);
    }
}
