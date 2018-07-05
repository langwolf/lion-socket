package com.lioncorp.server;

import java.util.ArrayList;
import java.util.List;

import com.lioncorp.common.util.ClassUtil;


public class ServiceInject implements Inject{


    @Override
    public <T> boolean inspect(Class<T> clazz) {
        return clazz.getAnnotation(LionImpl.class)!=null;
    }

    @Override
    public List<Class<?>> inject(String packageName) {
        List<Class<?>> list = new ArrayList<Class<?>>();
        List<Class<?>> classes = ClassUtil.getClasses(packageName);
        for (Class<?> clas :classes) {
            if(this.inspect(clas)){
                list.add(clas);
            }
        }
        return list;
    }   
}
