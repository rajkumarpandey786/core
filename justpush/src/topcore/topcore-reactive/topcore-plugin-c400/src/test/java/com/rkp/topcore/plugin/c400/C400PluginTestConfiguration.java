package com.rkp.topcore.plugin.c400;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan({
        "com.rkp.topcore.canonical",
        "com.rkp.topcore.core.xml",
        "com.rkp.topcore.plugin.c400"
})
public class C400PluginTestConfiguration {
}