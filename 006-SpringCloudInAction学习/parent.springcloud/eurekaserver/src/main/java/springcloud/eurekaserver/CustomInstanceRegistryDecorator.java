package springcloud.eurekaserver;

import java.util.List;

import org.springframework.cloud.netflix.eureka.server.InstanceRegistry;
import org.springframework.util.CollectionUtils;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.EurekaClientConfig;
import com.netflix.eureka.EurekaServerConfig;
import com.netflix.eureka.resources.ServerCodecs;

import lombok.extern.slf4j.Slf4j;

/**
* 自定义注册
*/
@Slf4j
public class CustomInstanceRegistryDecorator extends InstanceRegistry {

    private String profiles;

    private WhiteListThird whiteListThird;

    public CustomInstanceRegistryDecorator(EurekaServerConfig serverConfig, EurekaClientConfig clientConfig,
                                           ServerCodecs serverCodecs, EurekaClient eurekaClient,
                                           WhiteListThird whiteListThird) {
        super(serverConfig, clientConfig, serverCodecs, eurekaClient, 1, 1);
        this.whiteListThird = whiteListThird;
    }

    public void setWhiteListService(String profiles) {
        this.profiles = profiles;
    }

    @Override
    public void register(InstanceInfo info, int leaseDuration, boolean isReplication) {
        if (checkRegister(info)) {
            super.register(info, isReplication);
        }
    }

    @Override
    public void register(final InstanceInfo info, final boolean isReplication) {
        if (checkRegister(info)) {
            super.register(info, isReplication);
        }
    }


    public boolean checkRegister(InstanceInfo info) {
        Boolean allow = false;
        String registerIp = info.getIPAddr();
        //拦截黑名单
        if (BlackIpCache.isBlackIp(registerIp)) return allow;
        log.info("=====================custom=====================");
        List<String> ipLists = IpContextHandler.getInstance().getIpList();
        if (CollectionUtils.isEmpty(ipLists)) {
            // 通过第三方接口获取当前注册服务器的环境,并查出当前环境下的已分配到服务器
            whiteListThird.getInstanceIpList(IpContextHandler.getInstance().getLocalIp());
            return allow;
        }
        for (String ip : ipLists) {
            if (ip.equals(registerIp)) {
                allow = true;
            }
        }

        if (!allow) {
            BlackIpCache.add(registerIp);
            StringBuffer sb = new StringBuffer().append("客户端[" + info.getAppName())
                    .append("," + info.getInstanceId()).append("," + info.getIPAddr())
                    .append("," + info.getPort() + "被拒绝注册！");
            log.info(sb.toString());
        }
        return allow;
    }

}
