package com.example.kepco_mec_springboot.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.kepco_mec_springboot.model.ApplyCharge;
import com.example.kepco_mec_springboot.model.ChargerMap;
import com.example.kepco_mec_springboot.model.ChargerReport;
import com.example.kepco_mec_springboot.model.User;
import com.example.kepco_mec_springboot.repository.ApplyChargeRepository;
import com.example.kepco_mec_springboot.repository.ChargerMapRepository;
import com.example.kepco_mec_springboot.repository.ChargerReportRepository;
import com.example.kepco_mec_springboot.repository.StatRepository;
import com.example.kepco_mec_springboot.repository.UserRepository;

@RestController
@CrossOrigin(origins = "*")
public class MapController {
    @Autowired
    ChargerMapRepository chargerMapRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ChargerReportRepository chargerReportRepository;
    @Autowired
    ApplyChargeRepository applyChargeRepository;
    @Autowired
    StatRepository statRepository;

    // 지도에서 동일 위치 그룹핑
    @GetMapping("/api/location")
    public List<Map<String,Object>> location(
        @RequestParam("lat_start") float lat_start,
        @RequestParam("lat_end") float lat_end,
        @RequestParam("lng_start") float lng_start,
        @RequestParam("lng_end") float lng_end
    ) {
        List<Map<String,Object>> locations = chargerMapRepository.findGroupByAddrAndLatAndLngWithNativeQuery(lat_start, lat_end, lng_start, lng_end);

        return locations;
    }

    // 지도에서 주소로 찾기
    @PostMapping("/api/find")
    public List<ChargerMap> find(@RequestParam("addr") String addr) {
        return chargerMapRepository.findByAddr(addr);
    }

    // 지도에서 설정 범위 안에 있는 충전소 찾기
    @GetMapping("/api/finds")
    public List<ChargerMap> findsAll(
        @RequestParam("lat_start") float lat_start,
        @RequestParam("lat_end") float lat_end,
        @RequestParam("lng_start") float lng_start,
        @RequestParam("lng_end") float lng_end
    ) {
        List<ChargerMap> locations = chargerMapRepository.findAllByLatBetweenAndLngBetween(lat_start, lat_end, lng_start, lng_end);

        return locations;
    }

    // 지도에서 충전소 마커 또는 사이드바의 충전 요청 버튼으로 충전 요청하기
    @PostMapping("/api/request/{sessionId}")
    public String insertRequest(
        @PathVariable String sessionId,
        @RequestParam("lat") float lat,
        @RequestParam("lng") float lng
    ) {
        List<ApplyCharge> findUser = applyChargeRepository.findByUserId(userRepository.findByUserId(sessionId));

        if (sessionId.equals("undefined") == false & userRepository.findByUserId(sessionId) != null) {
            if (findUser.size() > 0) {
                if (findUser.get(findUser.size()-1).getPostEndDate() != null) {
                    ApplyCharge applyCharge = new ApplyCharge();
                    applyCharge.setUserId(userRepository.findByUserId(sessionId));
                    applyCharge.setPostStartDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    applyCharge.setLat(lat);
                    applyCharge.setLng(lng);
                    applyChargeRepository.save(applyCharge);

                    return "접수되었습니다";
                }
                else {
                    return "완료되지 않은 요청입니다";
                }
            }
            else {
                ApplyCharge applyCharge = new ApplyCharge();
                applyCharge.setUserId(userRepository.findByUserId(sessionId));
                applyCharge.setPostStartDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                applyCharge.setLat(lat);
                applyCharge.setLng(lng);
                applyChargeRepository.save(applyCharge);

                return "접수되었습니다";
            }
        }
        else {
            return "로그인 해주세요";
        }
    }

    // 지도에서 충전소 마커를 통해 고장 신고하기
    @PostMapping("/api/down/{sessionId}")
    public String downReport(
        @PathVariable String sessionId,
        @RequestParam("stchId") String stchId
    ) {
        List<ChargerReport> findStch = chargerReportRepository.findByStchId(chargerMapRepository.findByStchId(stchId).get(0));
        ChargerMap sizeStch = chargerMapRepository.findByStchId(stchId).get(0);

        if (sessionId.equals("undefined") == false & userRepository.findByUserId(sessionId) != null) {
            if (findStch.size() > 0) {
                if (findStch.get(chargerReportRepository.findByStchId(sizeStch).size()-1).getPostEndDate() == null) {
                    return "이미 신고된 충전기 입니다";
                }
                else {
                    return "";
                }
            }
            else {
                    ChargerReport chargerReport = new ChargerReport();
                    chargerReport.setStchId(chargerMapRepository.findByStchId(stchId).get(0));
                    chargerReport.setAddr(chargerMapRepository.findByStchId(stchId).get(0).getAddr());
                    chargerReport.setPostStartDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    chargerReport.setUserId(userRepository.findByUserId(sessionId));
                    chargerReportRepository.save(chargerReport);

                    ChargerMap chargerMap = chargerMapRepository.findByStchId(stchId).get(0);
                    chargerMap.setStat(statRepository.findByStat(5));
                    chargerMapRepository.save(chargerMap);
                    
                    return "접수되었습니다";
                }
        }
        else {
            return "로그인 해주세요";
        }
    }

    @GetMapping("/api/findUser")
    public List<User> findUser(@RequestParam("userId") String userId) {
        List<User> userList = new ArrayList<>();
        userList.add(userRepository.findByUserId(userId));
        return userList;
    }
}
