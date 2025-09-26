package com.unimag.services.mappers;

import com.unimag.api.dto.CabinDtos;
import com.unimag.dominio.entidades.Cabin;

public class CabinMapper {
    public static Cabin ToEntity(CabinDtos.CabinRequest request){
        return request.cabin();
    }
    public static CabinDtos.CabinResponse ToResponse(Cabin cabin){
        return new CabinDtos.CabinResponse(cabin);
    }

}
