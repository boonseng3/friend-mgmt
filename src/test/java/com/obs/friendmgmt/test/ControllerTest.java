package com.obs.friendmgmt.test;

import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@AutoConfigureWebClient
@ActiveProfiles("test")
public abstract class ControllerTest {
}
