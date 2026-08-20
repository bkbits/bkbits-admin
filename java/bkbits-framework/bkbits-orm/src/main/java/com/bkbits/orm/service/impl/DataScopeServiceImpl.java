package com.bkbits.orm.service.impl;

import com.bkbits.orm.IDataScope;
import com.bkbits.orm.service.DataScopeService;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.handle.Context;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DataScopeServiceImpl implements DataScopeService {
    private static final String ATTR_KEY = "DataScopeContext";
    private final List<IDataScope> dataScopes;
    private final Map<String, IDataScope> dataScopeMap;

    public DataScopeServiceImpl(
            @Inject List<IDataScope> dataScopes) {
        this.dataScopes = Collections.unmodifiableList(dataScopes);
        Map<String, IDataScope> dataScopeMap = new HashMap<>();
        for (IDataScope dataScope : dataScopes) {
            if (dataScopeMap.putIfAbsent(dataScope.dataScope(), dataScope) != null) {
                log.warn("重复的datascope数据域: {}", dataScope.dataScope());
            }
        }
        this.dataScopeMap = Collections.unmodifiableMap(dataScopeMap);
    }

    @Override
    public void beginDataScope(List<String> dataScopes) {
        Stack<List<IDataScope>> dataScopeStack = Context.current().attr(ATTR_KEY);
        if (dataScopeStack == null) {
            dataScopeStack = new Stack<>();
            Context.current().attrSet(ATTR_KEY, dataScopeStack);
        }
        dataScopeStack.push(dataScopes.stream()
                .map(name -> {
                    IDataScope dataScope = dataScopeMap.get(name);
                    if (dataScope == null) {
                        log.warn("数据域不存在: {}", name);
                    }
                    return dataScope;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList())
        );
    }

    @Override
    public void endDataScope() {
        Stack<List<IDataScope>> dataScopeStack = Context.current().attr(ATTR_KEY);
        if (dataScopeStack != null && !dataScopeStack.isEmpty()) {
            dataScopeStack.pop();
        }
    }

    @Override
    public List<IDataScope> getDataScopes() {
        return dataScopes;
    }
}
