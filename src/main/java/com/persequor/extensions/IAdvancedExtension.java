package com.persequor.extensions;

import java.util.Map;

public interface IAdvancedExtension extends IExtension {
    boolean isInitialized();
    boolean init(Map<String,Boolean> configurationFlags);
}
