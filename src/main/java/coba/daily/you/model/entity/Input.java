package coba.daily.you.model.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = Input.TABEL_INPUT)
@Data
public class Input {
    public static final String TABEL_INPUT = "tabel";
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TABEL_INPUT)
    @SequenceGenerator(name = TABEL_INPUT, sequenceName = "tabel_input_seq")

    private Integer id;
    private String name;
    private String pbirth;
    private String bdate;
    private String address;
    private String religion;
    private String email;
    private String note;
    private String pictureUrl;


}