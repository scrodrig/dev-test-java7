package com.persequor.extensions;

import com.persequor.model.Event;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ExtensionListenerTest {
    private ExtensionListener listener;
    private List<IExtension> extensions;
    private Map<Class<? extends IExtension>, Map<String, Boolean>> configurations;
    @Mock private IAdvancedExtension extension1;
    @Mock
    private IExtension extension2;
    @Mock
    private Event event;

    @Before
    public void setup() {
        extensions = Arrays.asList(extension1, extension2);
        configurations = new HashMap<>();
        configurations.put(extension1.getClass(), new HashMap<>());
        configurations.get(extension1.getClass()).put("deleteAfterUse", true);
        when(extension1.order()).thenReturn(1);
        configurations.put(extension2.getClass(), new HashMap<>());
        configurations.get(extension2.getClass()).put("deleteAfterUse", false);
        when(extension2.order()).thenReturn(2);
        listener = new ExtensionListener(extensions, configurations);
    }

    @Test
    public void happyPath() {
        listener.handle(event, 32);
        
        verify(extension1).execute(any());
        verify(extension2).execute(any());
    }

    @Test
    public void isNotConfigured() {
        configurations.remove(extension2.getClass());
        listener.handle(event, 32);

        verify(extension1).init(any());
    }

    @Test
    public void doNotReConfigure() {
        configurations.remove(extension2.getClass());
        when(extension1.isInitialized()).thenReturn(true);
        when(extension1.init(any())).thenReturn(false);

        listener.handle(event, 32);

        verify(extension1, never()).init(any());
    }
}
