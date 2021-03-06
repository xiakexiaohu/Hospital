package com.gs.controller;

import com.gs.bean.Admin;
import com.gs.common.Constants;
import com.gs.common.bean.ControllerResult;
import com.gs.common.bean.Pager;
import com.gs.common.bean.Pager4EasyUI;
import com.gs.common.util.EncryptUtil;
import com.gs.common.util.PagerUtil;
import com.gs.common.web.SessionUtil;
import com.gs.service.AdminService;
import com.mongodb.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.apache.commons.httpclient.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * Created by WangGenshen on 5/16/16.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    /**
     * 问卷服务器地址
     */
    private static String urlPrefix = "http://mbigdata.njust.edu.cn/manage/";
//    private static String urlPrefix = "http://localhost:8081/manage/";

    @Resource
    private AdminService adminService;

    @Resource
    private MongoTemplate mongoTemplate;//自动注入MongoTemplate

    @RequestMapping(value = "login_page", method = RequestMethod.GET)
    public String toLoginPage(Model model) {
        model.addAttribute(new Admin());
        return "admin/login";
    }

    @ResponseBody
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public ControllerResult login(Admin admin, @Param("checkCode") String checkCode, HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            return ControllerResult.getSuccessResult("登录成功");
        }
        String codeInSession = (String) session.getAttribute(Constants.SESSION_CHECK_CODE);
        if (checkCode != null && checkCode.equals(codeInSession)) {
            admin.setPwd(EncryptUtil.md5Encrypt(admin.getPwd()));
            System.out.println("admin.getPwd():" + admin.getPwd());
            Admin a = adminService.query(admin);
            if (a != null) {
                session.setAttribute(Constants.SESSION_ADMIN, a);
                return ControllerResult.getSuccessResult("登录成功");
            } else {
                return ControllerResult.getFailResult("登录失败,请检查邮箱或密码");
            }
        } else {
            return ControllerResult.getFailResult("验证码错误");
        }
    }

    @RequestMapping(value = "logout", method = RequestMethod.GET)
    public String logout(HttpSession session) {
        session.removeAttribute(Constants.SESSION_ADMIN);
        return "redirect:login_page";
    }


    /**
     * 建立连接，传递后缀参数
     *
     * @param urlSuffix
     */
    public void connectionUrl(String urlSuffix) {
        HttpClient client = new HttpClient();
        GetMethod method = new GetMethod(urlPrefix + urlSuffix);
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
        try {
            int statusCode = client.executeMethod(method);
            if (statusCode != org.apache.http.HttpStatus.SC_OK) {
                System.err.println("Method failed: " + method.getStatusLine());
            }
            byte[] responseBody = method.getResponseBody();
            System.out.println(new String(responseBody));

        } catch (HttpException e) {
            System.out.println("Fatal protocal violation:" + e.getMessage());
        } catch (IOException e) {
            System.err.println("Fatal transport error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            method.releaseConnection();
        }
    }

    @ResponseBody
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public ControllerResult add(Admin admin, HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            admin.setPwd(EncryptUtil.md5Encrypt(admin.getPwd()));
            int flagInsert = adminService.insert(admin);
            if (flagInsert == 1) {
                logger.info("Add admin successfully in MySQL");
                //由于问卷服务器在内网，所以采用url的方式GET方式提交
                String urlSuffix = "add?username=" + admin.getEmail() + "&password=" + admin.getPwd();
                //建立连接
                connectionUrl(urlSuffix);
                logger.info("Insert into MongoDB Successfully!!!");
                return ControllerResult.getSuccessResult("成功添加管理员");
            } else {
                return ControllerResult.getFailResult("添加失败，可能存在相同邮箱！！！");
            }
//            DBCollection mongoTemplateCollection = mongoTemplate.getCollection("admins");
//            BasicDBObject basicDBObject = new BasicDBObject();
//            basicDBObject.put("username", admin.getEmail());
//            basicDBObject.put("password", admin.getPwd());
//            mongoTemplateCollection.insert(basicDBObject);
        } else {
            return ControllerResult.getNotLoginResult("登录信息无效，请重新登录！！！");
        }
    }


    @RequestMapping(value = "home", method = RequestMethod.GET)
    public String home(HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            return "admin/home";
        } else {
            return "redirect:login_page";
        }
    }

    @RequestMapping(value = "list_page", method = RequestMethod.GET)
    public String toListPage(HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            return "admin/admins";
        } else {
            return "redirect:redirect_login_page";
        }
    }

    @ResponseBody
    @RequestMapping(value = "search_pager", method = RequestMethod.GET)
    public Pager4EasyUI<Admin> searchPager(@Param("page") String page, @Param("rows") String rows, Admin admin, HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            logger.info("show admin info by pager");
            int total = adminService.countByCriteria(admin);
            Pager pager = PagerUtil.getPager(page, rows, total);
            List<Admin> admins = adminService.queryByPagerAndCriteria(pager, admin);
            return new Pager4EasyUI<Admin>(pager.getTotalRecords(), admins);
        } else {
            logger.info("can not show admin info by pager cause admin is not login");
            return null;
        }
    }

    @RequestMapping(value = "query/{id}", method = RequestMethod.GET)
    public ModelAndView queryById(@PathVariable("id") String id, HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            ModelAndView mav = new ModelAndView("admin/info");
            Admin admin = adminService.queryById(id);
            mav.addObject("admin", admin);
            return mav;
        }
        return new ModelAndView("redirect:/admin/redirect_login_page");
    }

    @ResponseBody
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public ControllerResult update(Admin admin, HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            logger.info("update admin info successfully");
            adminService.update(admin);
            return ControllerResult.getSuccessResult("成功更新用户信息");
        } else {
            return ControllerResult.getNotLoginResult("登录信息无效，请重新登录");
        }
    }

    //json传递过来的值，需要采用如下@RequestParams/@Params获取
    @ResponseBody
    @RequestMapping(value = "delete", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ControllerResult deleteById(@RequestParam(value = "ids") String ids, @RequestParam(value = "emails") String emails, HttpSession session) {
        List<String> idsList = Arrays.asList(ids.split(","));
        List<String> emailsList = Arrays.asList(emails.split(","));
        //使用MongoTemplate连接mongodb数据库，进行操作
        DBCollection dbCollection = mongoTemplate.getCollection("admins");
        if (SessionUtil.isAdmin(session)) {
            adminService.deleteByIds(idsList);
            logger.info("Delete admin successfully in MySQL");
            //对mongodb进行删除
            //批量删除

            for (String email : emailsList) {
                String urlSuffix = "delete?username=" + email;
                connectionUrl(urlSuffix);
//                //A basic implementation of BSON object that is MongoDB specific.
//                // A DBObject can be created as follows, using this class:
//                BasicDBObject query = new BasicDBObject();
//                query.put("username", email);
//                dbCollection.remove(query);
            }
            logger.info("Delete admin successfully in MongDB");
            return ControllerResult.getSuccessResult("成功删除用户信息！！！");
        } else {
            return ControllerResult.getFailResult("删除失败用户信息！！！");
        }
    }


    @RequestMapping(value = "setting_page", method = RequestMethod.GET)
    public String settingPage(Admin admin, HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            return "admin/setting";
        } else {
            return "redirect:redirect_login_page";
        }
    }

    /**
     * 用户自己修改密码
     * @param pwd
     * @param newPwd
     * @param conPwd
     * @param session
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "update_pwd", method = RequestMethod.POST)
    public ControllerResult updatePwd(@Param("pwd") String pwd, @Param("newPwd") String newPwd, @Param("conPwd") String conPwd, HttpSession session) {
        if (SessionUtil.isAdmin(session)) {
            Admin admin = (Admin) session.getAttribute(Constants.SESSION_ADMIN);
            DBCollection mongoTemplateCollection = mongoTemplate.getCollection("admins");
            if (admin.getPwd().equals(EncryptUtil.md5Encrypt(pwd)) && newPwd != null && conPwd != null && newPwd.equals(conPwd)) {
                admin.setPwd(EncryptUtil.md5Encrypt(newPwd));
                adminService.updatePassword(admin);
                logger.info("Update MySQL Successfully!!!");
                //更新mongodb
                String urlSuffix = "update?username=" + admin.getEmail() + "&password=" + admin.getPwd();
                connectionUrl(urlSuffix);
                logger.info("Update Mongodb Successfully!!!");
                session.setAttribute(Constants.SESSION_ADMIN, admin);
                return ControllerResult.getSuccessResult("更新密码成功");
            } else {
                return ControllerResult.getFailResult("原密码错误,或新密码与确认密码不一致");
            }
        } else {
            return ControllerResult.getNotLoginResult("登录信息无效，请重新登录");
        }
    }

    /**
     * 管理员修改密码
     * @param admin
     * @param session
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "update_other_pwd", method = RequestMethod.POST)
    public ControllerResult updateOtherPwd(Admin admin, HttpSession session) {
        DBCollection mongoTemplateCollection = mongoTemplate.getCollection("admins");
        if (SessionUtil.isAdmin(session)) {
            admin.setPwd(EncryptUtil.md5Encrypt(admin.getPwd()));
            //由于mysql是根据id更新，所以传过来的email为null，我们需要传相应的email过来
            adminService.updatePassword(admin);
            logger.info("Update MySQL Successfully!!!");
            //更新mongodb
            String urlSuffix = "update?username=" + admin.getEmail() + "&password=" + admin.getPwd();
            connectionUrl(urlSuffix);
            logger.info("Update Mongodb Successfully!!!");
            return ControllerResult.getSuccessResult("更新密码成功");
        } else {
            return ControllerResult.getNotLoginResult("登录信息无效，请重新登录");
        }
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

}
