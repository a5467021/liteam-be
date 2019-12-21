package com.liteam.controller;

import com.jcraft.jsch.SftpException;
import com.liteam.entity.*;
import com.liteam.service.FriendService;
import com.liteam.service.UserService;
import com.liteam.common.JsonResult;
import com.liteam.entity.*;
import com.liteam.service.CommendService;
import com.liteam.service.GroupService;
import com.liteam.utils.BaseController;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

    @Resource
    private UserService userService;

    @Resource
    private GroupService groupService;

    @Resource
    private FriendService friendService;

    @Resource
    private CommendService commendService;

    private JsonResult jsonResult = new JsonResult();

    /**
     * 返回自己所有信息
     *
     * @param token
     * @return
     */
    @GetMapping(value = {"/me"})
    public Object allInformation(@RequestHeader("Authorization") String token){
        User user = getUser();
        User userFind = userService.findOne(user.getNumber());
        userFind.setPassword(null);
        Map<String, Object> map = new HashMap<>();
        map.put("user", userFind);
        List<Commend> commends = commendService.getCommends(user.getNumber());
        map.put("commends", commends);
        return jsonResult.success(map);
    }

    /**
     * 用户注册
     *
     * @param user
     * @return
     */
    @PostMapping(path = "/register")
    public Object register(@Valid @RequestBody User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            return jsonResult.failed(400, bindingResult.getFieldError().getDefaultMessage(), HttpStatus.BAD_REQUEST);
        }
        String pattern = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$";
        Pattern r = Pattern.compile(pattern);
        Matcher matcher = r.matcher(user.getPassword());
        if(!matcher.find()){
            return jsonResult.failed(400, "密码必须由6-16位字母和数字组成", HttpStatus.BAD_REQUEST);
        }
        if (user.getPhone()!=null && user.getPassword()!=null && user.getNickname()!=null) {
            String phone = user.getPhone();
            String nickname = user.getNickname();
            String password = user.getPassword();
            User userInsert = userService.register(nickname, password, phone);
            userInsert.setPassword(null);
            return jsonResult.success(userInsert);
        } else {
            return jsonResult.failed(400, "缺少参数", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 用户登录
     *
     * @param body
     * @return
     */
    @PostMapping("/login")
    public Object login(@RequestBody Map<String, Object> body) {
        if (body.containsKey("number") && body.containsKey("password")) {
            int number = (Integer) body.get("number");
            String password = (String) body.get("password");
            User userFind = userService.findOne(number);
            if (userFind == null){
                return jsonResult.failed(400, "账号不存在", HttpStatus.BAD_REQUEST);
            }
            User user = userService.checkUser(number, password);
            if (user == null){
                return jsonResult.failed(400, "账号或密码错误", HttpStatus.BAD_REQUEST);
            }
            String token = userService.generateToken(user);
            user.setPassword(null);
            Map<String, Object> map = new HashMap<>();
            map.put("user", user);
            map.put("token", token);
            List<Commend> commends = commendService.getCommends(user.getNumber());
            map.put("commends", commends);
            return jsonResult.success(map);
        } else {
            return jsonResult.failed(400, "缺少参数", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 通过信息查找用户
     * @param token
     * @param information
     * @return
     */
    @GetMapping(value = "/get_users_by_info")
    public Object getUsersByInformation(@RequestHeader("Authorization") String token, @RequestParam("information") String information){
        List<User> usersNickname = userService.findAllUsersByNickname(information);
        List<User> usersPhone = userService.findAllUsersByPhone(information);
        List<User> users = new ArrayList<>();
        users.addAll(usersNickname);
        users.addAll(usersPhone);
        Pattern pattern = Pattern.compile("^[0-9]{0,8}$");
        Matcher matcher = pattern.matcher(information);
        if (matcher.matches()){
            User user = userService.findOne(Integer.parseInt(information));
            if(user!=null){
                users.add(user);
            }
        }
        if(users == null){
            return jsonResult.failed(400, "查询不到该用户！", HttpStatus.BAD_REQUEST);
        }
        for (User userFind:users){
            userFind.setPassword(null);
        }
        return jsonResult.success(users);
    }

    /**
     * 根据number获得单个用户
     * @param token
     * @param number
     * @return
     */
    @GetMapping(value = "/get_user")
    public Object getOne(@RequestHeader("Authorization") String token, @RequestParam("number") int number){
        User mainUser = getUser();
        User user = userService.findOne(number);
        if (user == null){
            return jsonResult.failed(400, "查询不到该用户！", HttpStatus.BAD_REQUEST);
        }
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("number", user.getNumber());
        userMap.put("nickname", user.getNickname());
        userMap.put("phone", user.getPhone());
        userMap.put("nativePlace", user.getNativePlace());
        userMap.put("headUrl", user.getHeadUrl());
        userMap.put("birthday", user.getBirthday());
        userMap.put("sex", user.getSex());
        userMap.put("signature", user.getSignature());
        boolean isFriend = false;
        Friend friend = friendService.queryByNumber(number, mainUser.getNumber());
        if (friend!=null){
            isFriend = true;
        }
        userMap.put("isFriend", isFriend);
        Map<String, Object> map = new HashMap<>();
        map.put("user", userMap);
        List<Commend> commends = commendService.getCommends(mainUser.getNumber());
        map.put("commends", commends);
        return jsonResult.success(map);
    }

    /**
     * 修改头像
     * @param token
     * @return
     */
    @PutMapping(value = "/update_headUrl")
    public Object updateHeadUrl(@RequestHeader("Authorization") String token, UserFriend userFriend){
        User user = getUser();
        MultipartFile file = userFriend.getHeadUrl();
        try{
            byte[] bytes = file.getBytes();
            String name = file.getOriginalFilename();
            FileConverse fileConverse = new FileConverse();
            fileConverse.login();
            fileConverse.upload(name, bytes);
            String url = fileConverse.url();
            fileConverse.logout();
            User modifyUser = userService.modifyHead(user.getNumber(), url);
            modifyUser.setPassword(null);
            Map<String, Object> map = new HashMap<>();
            map.put("user", modifyUser);
            List<Commend> commends = commendService.getCommends(user.getNumber());
            map.put("commends", commends);
            return jsonResult.success(map);
        }
        catch (SftpException e){e.printStackTrace();}
        catch (IOException ex){ex.printStackTrace();}
        return jsonResult.success();
    }

    /**
     * 增加分组
     * @param token
     * @param body
     * @return
     */
    @PostMapping(value = "/add_group")
    public Object addGroup(@RequestHeader("Authorization") String token, @RequestBody Map<String, Object> body){
        if(body.containsKey("groupName")){
            User user = getUser();
            Group group = groupService.addGroup(user.getNumber(), (String)body.get("groupName"));
            return jsonResult.success(group);
        }else {
            return jsonResult.failed(400, "缺少参数", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 删除分组
     * @param token
     * @param body
     * @return
     */
    @DeleteMapping(value = "/delete_group")
    public Object removeGroup(@RequestHeader("Authorization") String token, @RequestBody Map<String, Object> body){
        User user = getUser();
        if(body.containsKey("groupId")) {
            String result = groupService.removeGroup(user.getNumber(), (int) body.get("groupId"));
            if(result == null){
                return jsonResult.failed(400, "此分组为默认分组，不可被删除", HttpStatus.BAD_REQUEST);
            }
            return jsonResult.success();
        }else {
            return jsonResult.failed(400, "缺少参数", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 修改分组名称
     * @param token
     * @param body
     * @return
     */
    @PutMapping(value = "/update_group_name")
    public Object modifyGroupName(@RequestHeader("Authorization") String token, @RequestBody Map<String, Object> body){
        User user = getUser();
        if(body.containsKey("groupId")&&body.containsKey("groupName")){
            int groupId = (int) body.get("groupId");
            String groupName = (String) body.get("groupName");
            if (groupService.getDefaultGroup(user.getNumber()).getGroupId() == groupId){
                return jsonResult.failed(400, "不可修改默认分组", HttpStatus.BAD_REQUEST);
            }
            Group group = groupService.modifyGroupName(groupId, groupName);
            return jsonResult.success(group);
        }else {
            return jsonResult.failed(400, "缺少参数", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 获取该用户的所有分组
     * @param token
     * @return
     */
    @GetMapping(value = "/groups")
    public Object getUserAllGroups(@RequestHeader("Authorization") String token){
        User user = getUser();
        return jsonResult.success(groupService.getUserGroups(user.getNumber()));
    }

    /**
     * 删除评价
     * @param token
     * @param body
     * @return
     */
    @DeleteMapping(value = "/delete_commend")
    public Object deleteCommend(@RequestHeader("Authorization") String token, @RequestBody Map<String, Object> body){
        if(body.containsKey("commendId")){
            int commendId = (int) body.get("commendId");
            commendService.deleteCommend(commendId);
            return jsonResult.success();
        }else {
            return jsonResult.failed(400, "缺少参数", HttpStatus.BAD_REQUEST);
        }
    }
}
