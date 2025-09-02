package com.example.app.config;


import graphql.language.IntValue;
import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.math.BigInteger;

@Configuration
public class GraphqlConfig {

    @Bean
    public GraphQLScalarType dateTimeScalar() {
        Coercing<Object, OffsetDateTime> coercing = new Coercing<>() {
            private OffsetDateTime toOffsetDateTime(Object input) {
                if (input instanceof OffsetDateTime) {
                    return (OffsetDateTime) input;
                }
                if (input instanceof Instant) {
                    return OffsetDateTime.ofInstant((Instant) input, ZoneOffset.UTC);
                }
                if (input instanceof String) {
                    try {
                        return OffsetDateTime.parse((String) input);
                    } catch (DateTimeParseException ex) {
                        throw new CoercingParseLiteralException("Invalid DateTime string: " + input);
                    }
                }
                throw new CoercingParseLiteralException("Cannot convert value to OffsetDateTime: " + input);
            }

            @Override
            public OffsetDateTime serialize(Object dataFetcherResult) {
                try {
                    return toOffsetDateTime(dataFetcherResult);
                } catch (Exception ex) {
                    throw new CoercingSerializeException("Unable to serialize DateTime: " + ex.getMessage());
                }
            }

            @Override
            public OffsetDateTime parseValue(Object input) {
                return toOffsetDateTime(input);
            }

            @Override
            public OffsetDateTime parseLiteral(Object input) {
                if (input instanceof StringValue) {
                    return toOffsetDateTime(((StringValue) input).getValue());
                }
                throw new CoercingParseLiteralException("Expected AST type `StringValue` for DateTime");
            }
        };

        return GraphQLScalarType.newScalar()
                .name("DateTime")
                .description("DateTime scalar that accepts java.time.Instant and java.time.OffsetDateTime")
                .coercing(coercing)
                .build();
    }

    @Bean
    public GraphQLScalarType longScalar() {
        Coercing<Object, Long> coercing = new Coercing<>() {
            private Long toLong(Object input) {
                if (input instanceof Number) {
                    return ((Number) input).longValue();
                }
                if (input instanceof BigInteger) {
                    return ((BigInteger) input).longValue();
                }
                if (input instanceof String) {
                    try {
                        return Long.parseLong((String) input);
                    } catch (NumberFormatException ex) {
                        throw new CoercingParseLiteralException("Invalid Long string: " + input);
                    }
                }
                throw new CoercingParseLiteralException("Cannot convert value to Long: " + input);
            }

            @Override
            public Long serialize(Object dataFetcherResult) {
                try {
                    return toLong(dataFetcherResult);
                } catch (Exception ex) {
                    throw new CoercingSerializeException("Unable to serialize Long: " + ex.getMessage());
                }
            }

            @Override
            public Long parseValue(Object input) {
                return toLong(input);
            }

            @Override
            public Long parseLiteral(Object input) {
                if (input instanceof IntValue) {
                    BigInteger value = ((IntValue) input).getValue();
                    return value.longValue();
                }
                if (input instanceof StringValue) {
                    return toLong(((StringValue) input).getValue());
                }
                throw new CoercingParseLiteralException("Expected AST type `IntValue` or `StringValue` for Long");
            }
        };

        return GraphQLScalarType.newScalar()
                .name("Long")
                .description("Custom Long scalar that maps to java.lang.Long")
                .coercing(coercing)
                .build();
    }

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer(GraphQLScalarType dateTimeScalar, GraphQLScalarType longScalar) {
        return wiringBuilder -> wiringBuilder
                .scalar(dateTimeScalar)
                .scalar(longScalar);
    }
}