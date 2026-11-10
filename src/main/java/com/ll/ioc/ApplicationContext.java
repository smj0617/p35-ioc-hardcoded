package com.ll.ioc;

import com.ll.domain.testPost.testPost.repository.TestPostRepository;
import com.ll.domain.testPost.testPost.service.TestFacadePostService;
import com.ll.domain.testPost.testPost.service.TestPostService;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 스프링 ApplicationContext 를 흉내 낸 IoC 컨테이너 v1.
 * 빈 생성 방법은 하드코딩하되, 한 번 만든 빈은 이름별로 보관해 싱글톤으로 돌려준다.
 */
public class ApplicationContext {
    /** 이름 → 이미 만들어진 빈 (싱글톤 보관소) */
    private final Map<String, Object> beans = new HashMap<>();

    /** 이름 → 빈을 만드는 방법 */
    private final Map<String, Supplier<Object>> beanFactories = new HashMap<>();

    public ApplicationContext() {
        beanFactories.put("testPostRepository", TestPostRepository::new);
        beanFactories.put("testPostService", () -> new TestPostService(genBean("testPostRepository")));
        beanFactories.put("testFacadePostService", () -> new TestFacadePostService(
                genBean("testPostService"),
                genBean("testPostRepository")
        ));
    }

    @SuppressWarnings("unchecked")
    public <T> T genBean(String beanName) {
        Object bean = beans.get(beanName);

        if (bean == null) {
            Supplier<Object> factory = beanFactories.get(beanName);

            if (factory == null) {
                throw new IllegalArgumentException("등록되지 않은 빈입니다: " + beanName);
            }

            bean = factory.get();
            beans.put(beanName, bean);
        }

        return (T) bean;
    }
}
