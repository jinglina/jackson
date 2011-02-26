package org.codehaus.jackson.map.contextual;

import java.io.IOException;
import java.util.*;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.module.SimpleModule;
import org.codehaus.jackson.map.type.TypeFactory;

/**
 * Tests to ensure that we can do contextual key serializers and
 * deserializers as well as value ser/deser.
 * 
 * @since 1.8
 */
public class TestContextualKeyTypes extends BaseMapTest
{
    /*
    /**********************************************************
    /* Helper classes
    /**********************************************************
     */

    static class ContextualKeySerializer
        extends JsonSerializer<String>
        implements ContextualSerializer<String>
    {
        protected final String _prefix;
    
        public ContextualKeySerializer() { this(""); }
        public ContextualKeySerializer(String p) {
            _prefix = p;
        }

        @Override
        public void serialize(String value, JsonGenerator jgen, SerializerProvider provider) throws IOException
        {
            if (_prefix != null) {
                value = _prefix + value;
            }
            jgen.writeFieldName(value);
        }
    
        @Override
        public JsonSerializer<String> createContextual(SerializationConfig config, BeanProperty property)
            throws JsonMappingException
        {
            return new ContextualKeySerializer(_prefix+":");
        }
    }
    
    /*
    /**********************************************************
    /* Unit tests, serialization
    /**********************************************************
     */

    public void testSimpleKeySer() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule("test", Version.unknownVersion());
        module.addKeySerializer(String.class, new ContextualKeySerializer("prefix"));
        mapper.registerModule(module);
        Map<String,Object> input = new HashMap<String,Object>();
        input.put("a", Integer.valueOf(3));
        String json = mapper.typedWriter(TypeFactory.mapType(HashMap.class, String.class, Object.class))
            .writeValueAsString(input);
        assertEquals("{\"prefix:a\":3}", json);
    }
    
    /*
    /**********************************************************
    /* Unit tests, deserialization
    /**********************************************************
     */

    /*
    public void testSimpleKeyDeser() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule("test", Version.unknownVersion());
        module.addKeySerializer(String.class, new ContextualKeySerializer("prefix"));
        mapper.registerModule(module);
        Map<String,Object> input = new HashMap<String,Object>();
        input.put("a", Integer.valueOf(3));
        String json = mapper.typedWriter(TypeFactory.mapType(HashMap.class, String.class, Object.class))
            .writeValueAsString(input);
        assertEquals("{\"prefix:a\":3}", json);
    }
*/
}
