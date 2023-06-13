package com.meteorinc.thegateway.infrastructure.util.safeoperation;

import lombok.extern.slf4j.Slf4j;

/**
 * Classe que realiza as operações seguras removendo o boiler plate.
 */
@Slf4j
public class SafeOperations {

    /**
     * Executa a operação passada pela interface funciona {@link Operation} e retorna uma
     * instancia de {@link OperationResponse}
     *
     * @param operation A operação a ser executada.
     * @return Uma instancia de {@link OperationResponse} para fazer o handling.
     */
    public static <O> OperationResponse<O> doOperationAndReturn(Operation<O> operation){
        try{
            final var result = operation.doOperation();
            return new OperationResponse<>(result);

        } catch (Exception e){
            log.error("Was not possible to conclude the operation.",e);
            return new OperationResponse<>(null);
        }
    }
}
