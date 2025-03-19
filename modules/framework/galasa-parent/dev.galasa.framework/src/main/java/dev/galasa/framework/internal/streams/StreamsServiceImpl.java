/*
 * Copyright contributors to the Galasa project
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package dev.galasa.framework.internal.streams;

import java.util.ArrayList;
import java.util.List;

import dev.galasa.framework.spi.streams.IStream;
import dev.galasa.framework.spi.streams.IStreamsService;
import dev.galasa.framework.spi.streams.StreamsException;

public class StreamsServiceImpl implements IStreamsService {

    @Override
    public List<IStream> getStreams() throws StreamsException {
        return new ArrayList<IStream>();
    }

}
