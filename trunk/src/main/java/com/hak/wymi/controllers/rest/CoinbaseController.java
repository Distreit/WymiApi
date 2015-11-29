package com.hak.wymi.controllers.rest;

import com.hak.wymi.controllers.rest.helpers.Constants;
import com.hak.wymi.controllers.rest.helpers.UniversalResponse;
import com.hak.wymi.persistance.managers.CoinbaseResponseManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

@RestController
public class CoinbaseController {
    @Autowired
    private CoinbaseResponseManager coinbaseResponseManager;

    @Value("${coinbase.ipAddress}")
    private String coinbaseIpAddress;

    @RequestMapping(value = {"coinbase"}, method = RequestMethod.POST, produces = Constants.JSON)
    public ResponseEntity<UniversalResponse> createComment(HttpServletRequest request) {
        if (request.getRemoteAddr().equals(coinbaseIpAddress)) {
            String body = getBody(request);
            coinbaseResponseManager.saveNewResponse(body);
            return new ResponseEntity<>(new UniversalResponse(), HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(new UniversalResponse(), HttpStatus.BAD_REQUEST);
    }

    private String getBody(HttpServletRequest request) {
        String body = "";
        if (request.getMethod().equals("POST")) {
            StringBuilder sb = new StringBuilder();
            BufferedReader bufferedReader = null;

            try {
                bufferedReader = request.getReader();
                char[] charBuffer = new char[128];
                int bytesRead;
                while ((bytesRead = bufferedReader.read(charBuffer)) != -1) {
                    sb.append(charBuffer, 0, bytesRead);
                }
            } catch (IOException ex) {
                // swallow silently -- can't get body, won't
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException ex) {
                        // swallow silently -- can't get body, won't
                    }
                }
            }
            body = sb.toString();
        }
        return body;
    }
}
