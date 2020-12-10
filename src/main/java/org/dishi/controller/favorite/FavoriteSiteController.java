package org.dishi.controller.favorite;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import org.dishi.entity.Site;
import org.dishi.entity.User;
import org.dishi.service.impl.SiteService;
import org.dishi.utils.UserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/favorites")
public class FavoriteSiteController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    SiteService siteService;

    @GetMapping("/sites.do")
    public ModelAndView sites() {
        return new ModelAndView("favorites.html");
    }

    @PostMapping("/addSite.do")
    @ResponseBody
    public Map<String, String> add(Site site) {
        Map<String, String> retVal = new HashMap<>();
        User user = UserUtil.getUserFromSession();
        if (user == null) {
            retVal.put("message", "failed");
            return retVal;
        }
        site.setUid(user.getId());
        try {
            siteService.addSite(site);
            retVal.put("message", "success");
        } catch (DuplicateKeyException e) {
            logger.info(e.getMessage());
            retVal.put("message", "hasWebSiteName");
        }
        return retVal;
    }

    @RequestMapping("querySiteById.do")
    @ResponseBody
    public Map<String, Object> querySites(@RequestParam(required = false, defaultValue = "1") Integer currentPage,
                                 @RequestParam(required = false, defaultValue = "6") Integer pageSize){
        PageHelper.startPage(currentPage, pageSize);
        List<Site> sites = siteService.querySiteById(UserUtil.getUserFromSession().getId());
        PageInfo<Site> p = new PageInfo<>(sites);

        Map<String, Object> retVal = new HashMap<>();
        retVal.put("sites", sites);
        retVal.put("p", p);

        System.out.println("页数:"+currentPage);
        System.out.println("每页数据量:"+pageSize);
        System.out.println(p.getPageNum());
        System.out.println(p.getPages());
        return retVal;
    }

    @RequestMapping("/deleteSiteById.do")
    @ResponseBody
    public String deleteSiteById(@RequestParam Integer id){
        siteService.deleteSiteById(id);
        return "success";
    }
}
