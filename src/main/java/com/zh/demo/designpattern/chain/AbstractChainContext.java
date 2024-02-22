/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zh.demo.designpattern.chain;

import com.zh.demo.designpattern.ApplicationContextHolder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public final class AbstractChainContext<REQUEST, RESPONSE> implements CommandLineRunner {

    private final static Map<String, List<AbstractChainHandler>> abstractChainHandlerContainer = new HashMap<>();

    public final static ThreadLocal threadLocal = new ThreadLocal<>();

    public void handler(String mark, REQUEST requestParam) {
        List<AbstractChainHandler> abstractChainHandlers = abstractChainHandlerContainer.get(mark);
        if (CollectionUtils.isEmpty(abstractChainHandlers)) {
            throw new RuntimeException(String.format("[%s] Chain of Responsibility ID is undefined.", mark));
        }
        for (AbstractChainHandler abstractChainHandler : abstractChainHandlers) {
            if(!abstractChainHandler.handler(requestParam)){
                break;
            }
        }
    }


    @Override
    public void run(String... args) {
        List<Class<?>> interfaces = getInterfacesInPackage("com.zh.demo.designpattern.chain.type");
        for (Class<?> interfaceType : interfaces) {
            Map<String, AbstractChainHandler> beansOfType = (Map<String, AbstractChainHandler>) ApplicationContextHolder.getBeansOfType(interfaceType);
            // 转成list
            List<AbstractChainHandler> sortedList = beansOfType.values().stream()
                    .sorted(Comparator.comparing(Ordered::getOrder))
                    .collect(Collectors.toList());
            int index = interfaceType.getName().lastIndexOf(".") + 1;
            abstractChainHandlerContainer.put(interfaceType.getName().substring(index), sortedList);
        }
    }

    public static List<Class<?>> getInterfacesInPackage(String packageName) {
        List<Class<?>> result = new ArrayList<>();
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            String path = packageName.replace('.', '/');
            Enumeration<URL> resources = classLoader.getResources(path);

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                File directory = new File(resource.getFile());
                File[] files = directory.listFiles();

                if (files != null) {
                    for (File file : files) {
                        if (file.getName().endsWith(".class")) {
                            String className = packageName + '.' + file.getName().replace(".class", "");
                            Class<?> clazz = Class.forName(className);

                            if (clazz.isInterface()) {
                                result.add(clazz);
                            }
                        }
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return result;
    }
}
