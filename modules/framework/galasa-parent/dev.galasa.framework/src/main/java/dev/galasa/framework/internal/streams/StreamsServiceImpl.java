/*
 * Copyright contributors to the Galasa project
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package dev.galasa.framework.internal.streams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import dev.galasa.framework.spi.ConfigurationPropertyStoreException;
import dev.galasa.framework.spi.IConfigurationPropertyStoreService;
import dev.galasa.framework.spi.streams.IStream;
import dev.galasa.framework.spi.streams.IStreamsService;
import dev.galasa.framework.spi.streams.StreamsException;

public class StreamsServiceImpl implements IStreamsService {

    private IConfigurationPropertyStoreService cpsService;
    private static final String TEST_STREAM_PREFIX = "test.stream.";
    private static final Log logger = LogFactory.getLog(StreamsServiceImpl.class);

    public StreamsServiceImpl(IConfigurationPropertyStoreService configurationPropertyStoreService) {
        this.cpsService = configurationPropertyStoreService;
    }

    @Override
    public List<IStream> getStreams() throws StreamsException {
        List<IStream> streamsList = new ArrayList<>();
        try {
            // Keys are in the form: test.stream.<stream-name>.property
            streamsList = handleStreamProperties(TEST_STREAM_PREFIX);

        } catch (ConfigurationPropertyStoreException e) {
            throw new StreamsException("Failed to get streams from the CPS", e);
        }
        return streamsList;
    }

    @Override
    public IStream getStreamByName(String streamName) throws StreamsException {

        List<IStream> streams = new ArrayList<>();
        IStream stream = null;

        try {

            String testStreamPrefix = TEST_STREAM_PREFIX + streamName + ".";
            streams = handleStreamProperties(testStreamPrefix);

            if (!streams.isEmpty()) {
                stream = streams.get(0);
            }

        } catch (ConfigurationPropertyStoreException e) {
            throw new StreamsException("Failed to get a stream with the given name from the CPS", e);
        }

        return stream;

    }

    private Stream createStreamFromProperties(String streamName, Map<String, String> streamProperties)
            throws StreamsException {
        Stream streamBean = new Stream();
        streamBean.setName(streamName);

        for (Map.Entry<String, String> streamProperty : streamProperties.entrySet()) {
            String key = streamProperty.getKey();
            String value = streamProperty.getValue();
            switch (key) {
                case "description":
                    streamBean.setDescription(value);
                    break;
                case "obr":
                    streamBean.setObrsFromCommaSeparatedList(value);
                    break;
                case "location":
                    streamBean.setTestCatalogUrl(value);
                    break;
                case "repo":
                    streamBean.setMavenRepositoryUrl(value);
                    break;
            }
        }
        return streamBean;
    }

    private List<IStream> handleStreamProperties(String testStreamPrefix)
            throws ConfigurationPropertyStoreException, StreamsException {
        List<IStream> streamsList = new ArrayList<>();

        Map<String, String> testStreamProperties = cpsService.getPrefixedProperties(testStreamPrefix);

        Map<String, Map<String, String>> groupedStreams = new HashMap<>();
        for (Map.Entry<String, String> entry : testStreamProperties.entrySet()) {
            String key = entry.getKey();
            String propertyWithoutTestStreamPrefix = key.substring(TEST_STREAM_PREFIX.length());

            int dotIndex = propertyWithoutTestStreamPrefix.indexOf('.');
            if (dotIndex > 0) {
                // Extract the stream name and the rest of the key e.g., "description" or
                // "data.repository.url"
                String streamName = propertyWithoutTestStreamPrefix.substring(0, dotIndex);
                String subKey = propertyWithoutTestStreamPrefix.substring(dotIndex + 1);

                // Get or create the sub-map for this stream
                Map<String, String> streamProps = groupedStreams.getOrDefault(streamName, new HashMap<>());
                streamProps.put(subKey, entry.getValue());
                groupedStreams.put(streamName, streamProps);
            }
        }

        // Convert the stream properties into a Stream object
        for (Map.Entry<String, Map<String, String>> streamEntry : groupedStreams.entrySet()) {
            String streamName = streamEntry.getKey();
            Map<String, String> streamProps = streamEntry.getValue();
            Stream streamBean = createStreamFromProperties(streamName, streamProps);
            streamsList.add(streamBean);
        }

        return streamsList;
    }

    @Override
    public void deleteStream(String streamName) throws StreamsException {
        
        String testStreamPrefix = TEST_STREAM_PREFIX + streamName + ".";
        Map<String, String> testStreamProperties;

        try {
            // Retrieve all properties associated with the stream
            testStreamProperties = cpsService.getPrefixedProperties(testStreamPrefix);
        } catch (ConfigurationPropertyStoreException e) {
            throw new StreamsException("Failed to retrieve properties for stream: " + streamName, e);
        }

        // List to keep track of successfully deleted properties
        List<String> deletedKeys = new ArrayList<>();

        // Going for all-or-nothing approach while deleting properties
        // If any property fails to delete, we should restore all the previous ones
        // This strategy is implemented so that no property is left dangling.
        for (Map.Entry<String, String> entry : testStreamProperties.entrySet()) {
            String key = entry.getKey();
            try {
                cpsService.deleteProperty(key);
                deletedKeys.add(key);
            } catch (ConfigurationPropertyStoreException deletionEx) {
                // Deletion failed: attempt to revert all previously deleted properties
                for (String deletedKey : deletedKeys) {
                    try {
                        // Restore the property using the original value from the backup map
                        cpsService.setProperty(deletedKey, testStreamProperties.get(deletedKey));
                    } catch (ConfigurationPropertyStoreException rollbackEx) {
                        logger.error("Rollback failed for property: " + deletedKey, rollbackEx);
                    }
                }
                // After attempting rollback, throw an exception to indicate the overall
                // deletion failure.
                throw new StreamsException(
                        "Failed to delete property " + key + ". All previously deleted properties have been reverted.",
                        deletionEx);
            }
        }
    }
}
