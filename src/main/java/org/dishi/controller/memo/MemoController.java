package org.dishi.controller.memo;

import org.dishi.controller.BaseController;
import org.dishi.entity.Memo;
import org.dishi.entity.User;
import org.dishi.message.MailMessage;
import org.dishi.quartz.MemoJob;
import org.dishi.quartz.QuartzManager;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class MemoController extends BaseController {

    /**
     * 日期转换器，能够将页面传递进来的日期进行解析
     */
    @InitBinder
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        binder.registerCustomEditor(
                Date.class,
                new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), true));

    }

    @GetMapping("/memo.do")
    public ModelAndView memo(){
        return new ModelAndView("memo.html");
    }

    @PostMapping("/saveMemo.do")
    public ModelAndView saveMemo(Memo memo, HttpSession session){
        User user = (User) session.getAttribute("user");
        memo.setCreatetime(new Date());
        memo.setUid(user.getId());
        memo.setState(0);
        memoService.addMemo(memo);

        QuartzManager.addJob(scheduler, MemoJob.class, MailMessage.createMemo(memo, user), memo);

        System.out.println("发送时间："+memo.getSendtime());

        return new ModelAndView("redirect:memo.do");
    }

    @GetMapping("/queryAllMemo.do")
    @ResponseBody
    public List<Map<String, Object>> queryAllMemo(HttpSession session){
        User user = (User) session.getAttribute("user");
        List<Memo> memoList = memoService.queryAllMemo(user.getId());

        List<Map<String, Object>> mapList = new ArrayList<>();
        for (Memo memo : memoList) {
            //将备忘录的信息存到这里
            Map<String, Object> resultMap = new HashMap<String, Object>();
            resultMap.put("nickName", user.getUsername());
            resultMap.put("createTime", memo.getCreatetime());
            resultMap.put("email", user.getEmail());
            resultMap.put("content", memo.getContent());
            resultMap.put("memoId", memo.getMid());
            resultMap.put("sendTime", memo.getSendtime());
            if (memo.getState() == 0) {
                resultMap.put("state", "未发送");
            } else if (memo.getState() == 1) {
                resultMap.put("state", "已发送");
            } else {
                resultMap.put("state", "已发送");

            }
            mapList.add(resultMap);
        }

        return mapList;
    }

    @PostMapping("/memo/updateMemo.do")
    @ResponseBody
    public Map<String, String> updateMemo(Memo memo, HttpSession session){
        Map<String, String> map = new HashMap<>();
        if(memoService.updateMemo(memo)>0){
            map.put("message", "success");
        }else {
            map.put("message", "failed");
        }
        memo = memoService.select(memo.getMid());
        User user = (User)  session.getAttribute("user");
        QuartzManager.modifyJobTime(scheduler, MailMessage.createMemo(memo, user), memo);
        return map;
    }

    @GetMapping("/memo/deleteMemo.do")
    public ModelAndView deleteMemo(@RequestParam Integer mid){
        memoService.deleteMemo(mid);
        return new ModelAndView("redirect:/memo.do");
    }
}
