// =======================================================
// Copyright Mylife.com Inc., 2014. All rights reserved.
//
// =======================================================

package com.klark.util;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import com.klark.common.AbstractEntity;
import com.klark.common.AbstractEntityDTO;

@Component
public class EmailUtil {

    public void sendMail(MailSender mailSender, String from, String to, String subject, String msg) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(msg);
        mailSender.send(message);
    }

    public static void populateHeader(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE");
        response.setHeader("Access-Control-Max-Age", "360");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
        response.setHeader("Access-Control-Allow-Credentials", "true");
    }

    public static List<AbstractEntityDTO> convertToDTO(List<? extends AbstractEntity> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return null;
        }
        List<AbstractEntityDTO> dtos = new ArrayList<AbstractEntityDTO>();
        for (AbstractEntity entity : entities) {
            dtos.add(ObjectUtils.convert(entity));
        }
        // TODO Auto-generated method stub
        return dtos;
    }

    public static List<AbstractEntity> convertToEntity(List<AbstractEntityDTO> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return null;
        }
        List<AbstractEntity> dtos = new ArrayList<AbstractEntity>();
        for (AbstractEntityDTO entity : entities) {
            dtos.add(ObjectUtils.convert(entity));
        }
        // TODO Auto-generated method stub
        return dtos;
    }
}