package coba.daily.you.controller.restapi;


import coba.daily.you.model.dto.InputDTO;
import coba.daily.you.model.entity.Input;
import coba.daily.you.repository.InputRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;


@RequestMapping("/upload")
public class ImageAPI {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private InputRepository inputRepository;

    private InputDTO mapToDTO(Input input) {
        InputDTO inputDTO = modelMapper.map(input, InputDTO.class);
        return inputDTO;
    }
}
