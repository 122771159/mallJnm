package com.jnm.mallJnm.controller;

import com.jnm.mallJnm.cache.CustomCacheManager;
import com.jnm.mallJnm.exception.ServerException;
import com.jnm.mallJnm.model.enums.ErrorEnum;
import com.jnm.mallJnm.util.UUIDFactory;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


@RestController
public class VerifyImageController {
    private final char[] codeSequence = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J',
            'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
            'X', 'Y', 'Z', '2', '3', '4', '5', '6', '7', '8', '9'};

    @Autowired
    CustomCacheManager cacheManager;

    @GetMapping("/verify")
    public Map<String, String> execute(@RequestParam(value = "width", required = false, defaultValue = "70") int width,
                                       @RequestParam(value = "height", required = false, defaultValue = "30") int height,
                                       @RequestParam(value = "codeCount", required = false, defaultValue = "4") int codeCount,
                                       @RequestParam(value = "family", required = false) String family, HttpServletResponse response) {
        if (width > 1000) width = 1000;
        if (height > 500) height = 500;
        if (codeCount > 10) codeCount = 10;
        BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        String code = paintImage(buffImg, width, height, codeCount, family);
        Cache cache = cacheManager.getCache("verifyImg",1000*60*5);
        if (cache == null) {
            throw new ServerException(ErrorEnum.CACHE_ERROR);
        }
        String key = UUIDFactory.random();
        cache.put(key, code);
        String imageStr;
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.setUseCache(false);
            ImageIO.write(buffImg, "jpeg", outputStream);
            imageStr = Base64.getEncoder().encodeToString(outputStream.toByteArray());
            //Base64.encode(outputStream.toByteArray());
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            throw new ServerException(ErrorEnum.VERIFY_IMG_ERROR);
        }
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("key", key);
        resultMap.put("image", "data:image/jpeg;base64," + imageStr);
        return resultMap;
    }

    private String paintImage(BufferedImage buffImg, int width, int height, int codeCount, String family) {
        int x = width / (codeCount + 1);
        int fontHeight = height - 2;
        int codeY = height - 4;

        Graphics2D g = buffImg.createGraphics();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        Font font = new Font(family, Font.PLAIN, fontHeight);
        g.setFont(font);

        g.setColor(Color.BLACK);
        g.drawRect(0, 0, width - 1, height - 1);

        Random random = new Random();
        StringBuilder randomCode = new StringBuilder();
        int red, green, blue;
        for (int i = 0; i < codeCount; i++) {
            red = random.nextInt(255);
            green = random.nextInt(255);
            blue = random.nextInt(255);
            g.setColor(new Color(red, green, blue));

            String strRand = String.valueOf(codeSequence[random.nextInt(codeSequence.length)]);
            g.drawString(strRand, i * x + 3, codeY);

            randomCode.append(strRand);
        }

        return randomCode.toString().toLowerCase();
    }
}
