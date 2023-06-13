package com.meteorinc.thegateway.infrastructure.util.safeoperation;

/**
 * Interface funcional que realiza uma operação para ser manipulada pela classe
 * {@link SafeOperations}
 *
 * @param <O> Retorno do metodo.
 */
@FunctionalInterface
public interface Operation<O> {
    O doOperation() throws Exception;
}
