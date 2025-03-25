/*
* Copyright contributors to the Galasa project
*
* SPDX-License-Identifier: EPL-2.0
*/
package dev.galasa.framework.mocks;

import java.util.ArrayList;
import java.util.List;

import dev.galasa.framework.spi.streams.IStream;
import dev.galasa.framework.spi.streams.IStreamsService;
import dev.galasa.framework.spi.streams.StreamsException;

public class MockIStreamsService implements IStreamsService {

    List<IStream> streams = new ArrayList<>();

    public MockIStreamsService(List<IStream> streams) {
        this.streams = streams;
    }

    @Override
    public List<IStream> getStreams() throws StreamsException {
        return streams;
    }

    @Override
    public IStream getStreamByName(String streamName) throws StreamsException {

        IStream streamToReturn = null;
        for (IStream stream : streams) {
            if (stream.getName().equals(streamName)) {
                streamToReturn = stream;
            }
        }

        return streamToReturn;

    }

}
