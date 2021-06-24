package coba.daily.you.controller.restapi;

import coba.daily.you.model.dto.InputDTO;
import coba.daily.you.model.entity.Input;
import coba.daily.you.repository.InputRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/team")
@CrossOrigin(origins = "http://localhost:3000")
public class InputAPI {
    @Autowired
    private InputRepository inputRepository;

    @Autowired
    private ModelMapper modelMapper;

    public InputAPI(InputRepository inputRepository, ModelMapper modelMapper) {
        this.inputRepository = inputRepository;
        this.modelMapper = modelMapper;
    }

    @GetMapping()
    public List<InputDTO> getList() {
        List<Input> inputList = inputRepository.findAll();
        List<InputDTO> inputDTOList =
                inputList.stream()
                        .map(in -> mapInputToInputDto(in))
                        .collect(Collectors.toList());
        return inputDTOList;
    }

    private InputDTO mapInputToInputDto(Input input) {
        InputDTO inputDTO = modelMapper.map(input, InputDTO.class);
        return inputDTO;
    }

    @GetMapping("/getImage/{id}")
    public String getImage(@PathVariable Integer id) throws IOException {
        Input input = inputRepository.findById(id).get();
        String userFolderPath = "D:/Laras/profile/";
//        String userFolderPath = "C:/Users/Lenovo/FullStackTech/ngu-test/src/assets/utils/images/";
        String pathFile = userFolderPath + input.getPictureUrl();
        Path paths = Paths.get(pathFile);
        byte[] foto = Files.readAllBytes(paths);
        String img = Base64.getEncoder().encodeToString(foto);
        return img;

    }



    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, value = "/save")
    public InputDTO editSaveData(@RequestPart(value="data", required = true) InputDTO inputDTO, @RequestPart(value="pictureUrl", required = false) MultipartFile file) throws Exception {
        Input input = modelMapper.map(inputDTO, Input.class);

        if (file == null){
            input.setPictureUrl(inputRepository.findById(inputDTO.getId()).get().getPictureUrl());
        } else {
            String userFolderPath = "D:/Laras/profile/";
//        String userFolderPath = "C:/Users/Lenovo/FullStackTech/ngu-test/src/assets/utils/images/";
            Path path = Paths.get(userFolderPath);
            Path filePath = path.resolve(file.getOriginalFilename());
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            input.setPictureUrl(file.getOriginalFilename());
        }

        input = inputRepository.save(input);
        InputDTO inputDTO1 = mapInputToInputDto(input);
        return inputDTO1;
    }

//    @PostMapping("/save")
//    public InputDTO saveData(@RequestBody InputDTO inputDTO) {
//        Input input = modelMapper.map(inputDTO, Input.class);
//        input = inputRepository.save(input);
//        InputDTO inputDTO1 = mapInputToInputDto(input);
//        return inputDTO1;
//    }

    @GetMapping("/{id}")
    public InputDTO getData(@PathVariable Integer id) {
        Input input = inputRepository.findById(id).get();
        InputDTO inputDTO = new InputDTO();
        modelMapper.map(input, inputDTO);
        inputDTO.setId(input.getId());
        return  inputDTO;
    }

//    @DeleteMapping
//    public void del(){
//        inputRepository.deleteAll();
//    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        inputRepository.deleteById(id);
    }

}
