# 背景

有个需求，原先只涉及到一种A情况设备的筛选，每次筛选会经过多个流程，比如先a功能，a功能通过再筛选b功能，然后再筛选c功能，以此类推。现在新增了另外一种B情况的筛选，B情况同样需要A情况的筛选流程，并且需要在A情况的基础上，新增另外的功能筛选，这里假设A需要a、b、c功能的筛选，而B需要a、b、c、d功能的筛选，并且这些功能的筛选的顺序可能发生变动，比如新增了某个筛选，这个筛选涉及到的计算量少那肯定可以把这个置在前面先处理，不满足条件就return，咋一看，这个需求很符合责任链模式的应用场景，下面介绍编码。这里的代码参考了 *马丁*玩编程  在其12306项目里面的责任链模式，并做出一些相应改动，以适配当前的场景。

# 代码

## 责任链模式顶层接口

这里继承了Ordered类，是为了方便后续对处理器进行排序。

```java
public interface AbstractChainHandler<REQUEST> extends Ordered {

    default boolean handler(REQUEST requestParam){

        return true;
    };

}
```

## A情况的接口和B情况的接口。

```java
public interface DeviceTypeAChainFilter extends AbstractChainHandler<DeviceFilterBO> {

}
```

```java
public interface DeviceTypeBChainFilter extends AbstractChainHandler<DeviceFilterBO> {

}
```

定义成接口，后续往里面添加处理器的时候，方便查看当前A规则和B规则都有哪些处理器：

![image-20240206171639069](https://img2023.cnblogs.com/blog/2737325/202402/2737325-20240222112032821-211289405.png)

## 具体的处理器

### 处理器1：

```java
@Component
public class DeviceFunctionChainHandler implements DeviceTypeAChainFilter, DeviceTypeBChainFilter {

    @Override
    public boolean handler(DeviceFilterBO deviceFilterBO) {
        if (deviceFilterBO.getDeviceBO().getCondition() % 2 == 0) {
            System.out.println("处理器A：筛选功能不通过");
            return false;
        }
        // 筛选功能
        System.out.println("处理器A：筛选功能通过");
        return true;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
```

### 处理器2：

```java
@Component
public class DeviceResolutionChainHandler implements DeviceTypeAChainFilter, DeviceTypeBChainFilter {

    @Override
    public boolean handler(DeviceFilterBO deviceFilterBO) {
        // 分辨率支持
        System.out.println("处理器B：分辨率支持");
        return true;
    }

    @Override
    public int getOrder() {
        return 10;
    }

}
```

### 处理器3：

```java
@Component
public class DeviceCaculateOutputChainHandler implements DeviceTypeBChainFilter {

    @Override
    public boolean handler(DeviceFilterBO deviceFilterBO) {
        // 接口支持
        System.out.println("处理器C：输出接口支持");
        // 计算设备数量满足要求
        System.out.println("处理器C：根据输出接口计算的设备数量满足要求");
        return true;
    }

    @Override
    public int getOrder() {
        return 30;
    }
}
```

### 处理器4：

```java
@Component
public class DeviceCaculateInputChainHandler implements DeviceTypeAChainFilter, DeviceTypeBChainFilter {

    @Override
    public boolean handler(DeviceFilterBO deviceFilterBO) {
        if (deviceFilterBO.getDeviceBO().getCondition() % deviceFilterBO.getCondition() == 0) {
            System.out.println("处理器D：输入接口不支持");
            return false;
        }
        ArrayList<DeviceBO> deviceRes = (ArrayList<DeviceBO>) AbstractChainContext.threadLocal.get();
        deviceRes.add(deviceFilterBO.getDeviceBO());
        // 接口支持
        System.out.println("处理器D：输入接口支持");
        // 计算设备数量满足要求
        System.out.println("处理器D：根据输入接口计算的设备数量满足要求");
        return true;
    }

    @Override
    public int getOrder() {
        return 40;
    }
}
```

可以看到，处理器都用@Component进行标识，后续通过ioc容器获取这些处理器进行分类和执行。并且，可以看到A..filter接口有三个实现者，这说明A有三种处理器，同理B有四种处理器，并且由于顶层接口继承了Order类，所有具体的处理器都会标识当前的order，如上面的10，20，30...这里把Order的数字间隔放大一些，比如10，20，30，如果以后要往这些间隔插入新的处理逻辑也方便。

## 获取具体处理器和执行hanlder的上下文类

先将不同的处理规则的接口都放在某个特定包下

![image-20240206172405349](https://img2023.cnblogs.com/blog/2737325/202402/2737325-20240222112033350-817229401.png)

先去扫描这个包下的所有接口，然后再去Spring Ioc容器里面拿出这些接口的实现类，把不同的接口实现类按接口名字作为标识，按Order对这些实现类进行排序，然后放到一个List里面，以接口名字作为key，实现类List作为value，后续调用链式调用的时候，传入具体的接口名字（处理规则名字），实现链式顺序调用，具体实现如下

AbstractChainContext上下文类：

```java
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
```

在上面变量中，用了个  public final static ThreadLocal threadLocal = new ThreadLocal<>(); 这个是用来保存设备的筛选列表。

定义好不同筛选规则的枚举类：

```java
public enum DeviceChainMarkEnum {

    /**
     * A设备过滤器
     */
    DEVICE_TYPEA_FILTER("DeviceTypeAChainFilter"),

    /**
     * B设备过滤器
     */
    DEVICE_TYPEB_FILTER("DeviceTypeBChainFilter");

    String name;

    public String getName() {
        return name;
    }

    DeviceChainMarkEnum(String name) {
        this.name = name;
    }

}
```

Service的编写

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class DemoServiceImpl implements DemoService {

    private final AbstractChainContext<DeviceFilterBO, Object> devcieTypeChainContext;

    @Override
    public List<DeviceBO> filterDeviceTypeA(ParmDTO parmDTO) {
        ArrayList<DeviceBO> deviceList = new ArrayList<>();
        // 简化条件
        parmDTO.setCondition(2);
        // 实际情况应该是从数据库读取设备的信息
        for (int i = 0; i < 5; i++) {
            DeviceBO deviceDTO = DeviceBO.builder().condition(new Random().nextInt(100)).build();
            deviceList.add(deviceDTO);
        }
        ArrayList<DeviceBO> deviceRes = new ArrayList<>();
       // 把需要的结果放到threadLocal中,在具体的处理器中对结果List进行处理
        AbstractChainContext.threadLocal.set(deviceRes);
        // 筛选多个设备 对符合的设备加入到deviceRes
        for (DeviceBO deviceBo : deviceList) {
            DeviceFilterBO deviceFilterBO = DeviceFilterBO.builder().condition(parmDTO.getCondition()).deviceBO(deviceBo).build();
            // 以A规则进行处理
            devcieTypeChainContext.handler(DeviceChainMarkEnum.DEVICE_TYPEA_FILTER.getName(), deviceFilterBO);
        }
        AbstractChainContext.threadLocal.remove();
        System.out.println("筛选结果数量：" + deviceRes.size());
        return deviceRes;
    }

    @Override
    public List<DeviceBO> filterDeviceTypeB(ParmDTO parmDTO) {
        ArrayList<DeviceBO> deviceList = new ArrayList<>();
        // 简化条件
        parmDTO.setCondition(2);
        // 实际情况应该是从数据库读取设备的信息
        for (int i = 0; i < 5; i++) {
            DeviceBO deviceDTO = DeviceBO.builder().condition(new Random().nextInt(100)).build();
            deviceList.add(deviceDTO);
        }
        ArrayList<DeviceBO> deviceRes = new ArrayList<>();
        // 把需要的结果放到threadLocal中,在具体的处理器中对结果List进行处理
        AbstractChainContext.threadLocal.set(deviceRes);
        // 筛选多个设备 对符合的设备加入到deviceRes
        for (DeviceBO deviceBo : deviceList) {
            DeviceFilterBO deviceFilterBO = DeviceFilterBO.builder().condition(parmDTO.getCondition()).deviceBO(deviceBo).build();
            // 以B规则进行处理
            devcieTypeChainContext.handler(DeviceChainMarkEnum.DEVICE_TYPEB_FILTER.getName(), deviceFilterBO);
        }
        AbstractChainContext.threadLocal.remove();
        System.out.println("筛选结果数量：" + deviceRes.size());
        return deviceRes;
    }

}
```

这里假设有五种设备，每个设备通过DeviceBO里面的condition设置条件，演示一遍筛选过程

DeviceBO类：

```java
@Builder
@Data
public class DeviceBO {

    private int condition;

}
```

演示筛选规则A，一共五个设备数据，只有一个筛选通过了，这里涉及到A,B,D三种处理器

![image-20240222103239661](https://img2023.cnblogs.com/blog/2737325/202402/2737325-20240222112033744-1308343532.png)

演示筛选规则B，一共五个设备数据，2个筛选通过了，这里涉及到A,B,C,D三种处理器

![image-20240222103338406](https://img2023.cnblogs.com/blog/2737325/202402/2737325-20240222112034102-1798764143.png)

# 源码

[Johnynzh/chain-of-responsibility-demo: 责任链模式与spring容器的搭配应用 (github.com)](https://github.com/Johnynzh/chain-of-responsibility-demo)
