package com.demo.services.impl;

import com.demo.repositories.BlackListTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class DynamicBlacklistScheduler {
    @Autowired
    private TaskScheduler taskScheduler;
    @Autowired
    private BlackListTokenRepository blackListTokenRepository;

    @Transactional
    public void scheduleTokenDeletion(String tokenId, LocalDateTime expiryDate) {
//        long delay = expiryDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
//
//        taskScheduler.schedule(() -> {
//            // Gọi phương thức xóa trong một giao dịch
//            deleteTokenFromBlacklist(tokenId);
//        }, new Date(delay));
    }

    @Transactional
    public void deleteTokenFromBlacklist(String tokenId) {
        System.out.println(tokenId);
        blackListTokenRepository.deleteById(tokenId);
        System.out.println("Token " + tokenId + " đã bị xóa vào: " + LocalDateTime.now());
    }
}
