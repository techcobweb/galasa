/*
 * Copyright contributors to the Galasa project
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package dev.galasa.framework;

import static org.assertj.core.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.*;

import org.assertj.core.api.ByteArrayAssert;
import org.junit.Test;

import dev.galasa.framework.mocks.*;
import dev.galasa.framework.spi.IDynamicStatusStoreService;
import dev.galasa.framework.spi.utils.GalasaGson;

public class TestRunImpl {

    @Test
    public void testCanCreateARunImplWithNothingInDss() throws Exception  {
        String name = "U1234";
        Map<String,String> properties = new HashMap<String,String>();
        IDynamicStatusStoreService dss = new MockDSSStore(properties);
        new RunImpl(name,dss);
    }

    @Test
    public void testCanCreateARunImplWithARunInDss() throws Exception  {
        String name = "U1234";
        Map<String,String> dssProps = new HashMap<String,String>();

        Set<String> tagsValueGoingIn = new HashSet<String>();
        tagsValueGoingIn.add("tag1");
        tagsValueGoingIn.add("tag2");
        GalasaGson gson = new GalasaGson();
        String tagsAsJsonString = gson.toJson(tagsValueGoingIn);
        dssProps.put("run.U1234"+".tags", tagsAsJsonString);
        IDynamicStatusStoreService dss = new MockDSSStore(dssProps);

        RunImpl run = new RunImpl(name,dss);
        Set<String> tagsGotBack = run.getTags();

        assertThat( tagsGotBack).containsExactly("tag1","tag2");
    }

}