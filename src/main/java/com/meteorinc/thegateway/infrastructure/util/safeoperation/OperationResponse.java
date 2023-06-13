package com.meteorinc.thegateway.infrastructure.util.safeoperation;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * Classe que contem a resposta de uma operação executada de {@link SafeOperations}
 */
@Slf4j
public class OperationResponse<O> {

    private O output;

    public OperationResponse() {
    }

    public OperationResponse(O output) {
        this.output = output;
    }

    /**
     * Caso nao tenha sido possivel realizar a operação passada com sucesso, sera retornado um valor alternativo.
     * @param alternativeValue Valor alternativo em caso de exception durante o processamento.
     *
     * @return A resposta do processamento.
     */
    public O ifNotPossible(final O alternativeValue){
        if(!Objects.isNull(this.output)){
            return output;
        }

        return alternativeValue;
    }


    /**
     * Caso nao tenha sido possivel realizar a operação passada com sucesso, sera chamado uma operação
     * alternativa.
     * @param alternativeOperation Operação alternativa em caso de exception durante o processamento.
     *
     * @return A resposta do processamento.
     */
    public O ifNotPossibleDo(@NonNull final Operation<O>  alternativeOperation){
        try{
            return alternativeOperation.doOperation();
        }catch (Exception exception){
            log.error("Was not possible to conclude the fallback.", exception);
            return null;
        }

    }

    public O getOutput() {
        return output;
    }
}
