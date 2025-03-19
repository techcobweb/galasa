/*
 * Copyright contributors to the Galasa project
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package dev.galasa.framework.api.streams;

import dev.galasa.framework.api.beans.generated.Stream;
import dev.galasa.framework.api.beans.generated.StreamMetadata;
import dev.galasa.framework.api.common.resources.GalasaResourceValidator;

public class StreamsTransform {
   
    public Stream createStreamBean() {

        Stream streamBean = new Stream();
        streamBean.setApiVersion(GalasaResourceValidator.DEFAULT_API_VERSION);
        streamBean.setkind("GalasaStream");

        return streamBean;
    }

    private StreamMetadata createStreamMetaData(Stream stream) {

        StreamMetadata metaData = new StreamMetadata();

        

        return metaData;

    }
}
