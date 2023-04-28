package ru.pchelicam.addresssearcher.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Aspect
@Component
public class ServicesAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServicesAspect.class);

    @Pointcut("execution(* ru.pchelicam.addresssearcher.service.*.*(..)) && !@annotation(ru.pchelicam.addresssearcher.aspect.NoLogging)")
    public void addressSearcherServicesPointcut() {}

    @Around("addressSearcherServicesPointcut()")
    public void logMethod(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();

        LOGGER.info("Method {} is called",  signature.getMethod().getName());
        Instant startTime = Instant.now();
        Object object = point.proceed();
        Instant endTime = Instant.now();
        LOGGER.info("Time execution of method {} is {} ms", signature.getMethod().getName(), Duration.between(startTime, endTime).toMillis());
    }

}
