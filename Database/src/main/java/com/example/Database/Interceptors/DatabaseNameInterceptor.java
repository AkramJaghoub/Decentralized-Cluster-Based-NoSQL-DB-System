//package com.example.Database.Interceptors;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerInterceptor;
//
//@Component
//public class DatabaseNameInterceptor implements HandlerInterceptor {
//    @Autowired
//    private IndexInitializationService indexInitializationService;
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
//        String dbName = request.getHeader("db_name");
//        if(dbName != null && !dbName.trim().isEmpty()) {
//            indexInitializationService.initializeOnce(dbName);
//        }
//        //System.out.println("Warning: db_name header is missing.");
//        return true;
//    }
//}
