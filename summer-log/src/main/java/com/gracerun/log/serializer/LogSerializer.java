
package com.gracerun.log.serializer;

public interface LogSerializer {

    String write(Object[] args);

    String write(Object retVal);

}
