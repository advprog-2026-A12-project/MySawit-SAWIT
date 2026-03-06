package id.ac.ui.cs.advprog.mysawit.delivery.dto;

import lombok.Data;

@Data
public class CreateDeliveryRequest {
   private String supirId;
   private String harvestId;
   private Double payloadKg;
   private String supirName;
}
