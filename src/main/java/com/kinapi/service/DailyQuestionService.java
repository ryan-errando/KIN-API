package com.kinapi.service;

import com.kinapi.common.entity.BaseResponse;
import com.kinapi.common.entity.DailyQuestion;
import com.kinapi.common.entity.FamilyGroups;
import com.kinapi.common.entity.Users;
import com.kinapi.common.util.UserAuthHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor

public class DailyQuestionService {

    public BaseResponse getDailyQuestion(DailyQuestion dailyQuestion) {

        Users user = UserAuthHelper.getUser();
        FamilyGroups familyGroups = user.getFamilyGroups();

        if (familyGroups != null) {
            log.info("[getDailyQuestion] {} is trying to get today's daily question", familyGroups);

        }
    }
}
