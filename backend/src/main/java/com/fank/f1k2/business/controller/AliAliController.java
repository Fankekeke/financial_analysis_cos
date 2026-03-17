package com.fank.f1k2.business.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fank.f1k2.business.entity.UserInfo;
import com.fank.f1k2.business.service.IUserInfoService;
import com.fank.f1k2.common.utils.R;
import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationOutput;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationOutput;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.google.common.base.Throwables;
import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.alibaba.dashscope.utils.JsonUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/business/ai/")
public class AliAliController {

    @Resource
    private Generation generation;

    @Resource
    private IUserInfoService userInfoService;



    /**
     * ai问答
     *
     * @param content 用书输入文本内容
     */
    @GetMapping(value = "aliTyqw")
    public String send(@RequestBody String content) throws NoApiKeyException, InputRequiredException {
        //用户与模型的对话历史。list中的每个元素形式为{“role”:角色, “content”: 内容}。
        Message userMessage = Message.builder()
                .role(Role.USER.getValue())
                .content(content + "总是给出简介的回答（100-300字）")
                .build();

        GenerationParam param = GenerationParam.builder()
                //指定用于对话的通义千问模型名
                .model("qwen-plus")
                .messages(Arrays.asList(userMessage))
                //
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                //生成过程中核采样方法概率阈值，例如，取值为0.8时，仅保留概率加起来大于等于0.8的最可能token的最小集合作为候选集。
                // 取值范围为（0,1.0)，取值越大，生成的随机性越高；取值越低，生成的确定性越高。
                .topP(0.8)
                //阿里云控制台DASHSCOPE获取的api-key
                .apiKey("sk-fkeeb84a761e0941ad8b8bc9519d419dc0")
                //启用互联网搜索，模型会将搜索结果作为文本生成过程中的参考信息，但模型会基于其内部逻辑“自行判断”是否使用互联网搜索结果。
                .enableSearch(true)
                .build();
        GenerationResult generationResult = generation.call(param);
//        String json = generationResult.getOutput().getChoices().get(0).getMessage().getContent();
        // 获取所有 content 内容并放入 List 中
        List<String> allContents = generationResult.getOutput().getChoices().stream()
                .map(choice -> choice.getMessage().getContent())
                .collect(Collectors.toList());

        String combinedContent = String.join("\n---\n", allContents); // 使用 "---" 分隔多个回复内容
        return combinedContent;
    }

    @PostMapping(value = "recognitionImage")
    public R recognitionImage(@RequestParam("avatar") MultipartFile file) throws NoApiKeyException, InputRequiredException {
        try {

            // 将图片转为base64字符串
            String imageBase64 = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(file.getBytes());

            // 3. 构建用户消息
            MultiModalMessage userMessage = MultiModalMessage.builder().role(Role.USER.getValue())
                    .content(Arrays.asList(new HashMap<String, Object>(){{put("image", imageBase64);}},
                            new HashMap<String, Object>(){{put("text", "介绍此？请描述它的特征需要进行哪些步骤，以及各部分价格，600字以内。");}})).build();

            MultiModalConversationParam param = MultiModalConversationParam.builder()
                    .model(MultiModalConversation.Models.QWEN_VL_PLUS)
                    .message(userMessage)
                    .apiKey("sk-fkeeb84a761e0941ad8b8bc9519d419dc0")
                    .build();
            MultiModalConversation conv = new MultiModalConversation();
            MultiModalConversationResult result = conv.call(param);
            MultiModalConversationOutput.Choice ss = result.getOutput().getChoices().get(0);
            String combinedContent = ss.getMessage().getContent().get(0).get("text").toString(); // 合并多个回复内容
            return R.ok(combinedContent);

        } catch (Exception e) {
            System.err.println("API 调用失败: " + e.getMessage());
            e.printStackTrace();
            return R.error(e.getMessage());
        }
    }
}