package br.com.ms.custom.loggers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ApplicationInfo {

    private String name;
    private String version;
    private String groupId;

}
