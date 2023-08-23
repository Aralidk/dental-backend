package backend.dental.entity;

import backend.dental.enums.WorkStatus;
import backend.dental.worker.Worker;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "work_entity")
public class WorkEntity {
    @Id
    private Long id;
    private int teethNumber;
    private boolean metal;
    private boolean metalAbove;
    private boolean metalProva;
    private boolean zirkon;
    private boolean zirkonAbove;
    private boolean zirkonProva;
    private boolean bridge;
    private boolean EMax;
    private boolean temp;
    private boolean nigthPlaque;
    private boolean hard;
    private boolean protez;
    private int slideAmount;
    private int kronAmount;
    private boolean repair;
    private boolean setBottom;
    private boolean cageBottom;
    private String doldarBar;
    private String doldarFoot;
    private String customerNote;
    private String labNote;

    private BigDecimal price; // Fiyat alanı

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @ManyToOne
    @JoinColumn(name = "assigned_user_id")
    private Worker assignedUser;

    @Enumerated(EnumType.STRING)
    private WorkStatus workStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getTeethNumber() {
        return teethNumber;
    }

    public void setTeethNumber(int teethNumber) {
        this.teethNumber = teethNumber;
    }

    public boolean isMetal() {
        return metal;
    }

    public void setMetal(boolean metal) {
        this.metal = metal;
    }

    public boolean isMetalAbove() {
        return metalAbove;
    }

    public void setMetalAbove(boolean metalAbove) {
        this.metalAbove = metalAbove;
    }

    public boolean isMetalProva() {
        return metalProva;
    }

    public void setMetalProva(boolean metalProva) {
        this.metalProva = metalProva;
    }

    public boolean isZirkon() {
        return zirkon;
    }

    public void setZirkon(boolean zirkon) {
        this.zirkon = zirkon;
    }

    public boolean isZirkonAbove() {
        return zirkonAbove;
    }

    public void setZirkonAbove(boolean zirkonAbove) {
        this.zirkonAbove = zirkonAbove;
    }

    public boolean isZirkonProva() {
        return zirkonProva;
    }

    public void setZirkonProva(boolean zirkonProva) {
        this.zirkonProva = zirkonProva;
    }

    public boolean isBridge() {
        return bridge;
    }

    public void setBridge(boolean bridge) {
        this.bridge = bridge;
    }

    public boolean isEMax() {
        return EMax;
    }

    public void setEMax(boolean EMax) {
        this.EMax = EMax;
    }

    public boolean isTemp() {
        return temp;
    }

    public void setTemp(boolean temp) {
        this.temp = temp;
    }

    public boolean isNigthPlaque() {
        return nigthPlaque;
    }

    public void setNigthPlaque(boolean nigthPlaque) {
        this.nigthPlaque = nigthPlaque;
    }

    public boolean isHard() {
        return hard;
    }

    public void setHard(boolean hard) {
        this.hard = hard;
    }

    public boolean isProtez() {
        return protez;
    }

    public void setProtez(boolean protez) {
        this.protez = protez;
    }

    public int getSlideAmount() {
        return slideAmount;
    }

    public void setSlideAmount(int slideAmount) {
        this.slideAmount = slideAmount;
    }

    public int getKronAmount() {
        return kronAmount;
    }

    public void setKronAmount(int kronAmount) {
        this.kronAmount = kronAmount;
    }

    public boolean isRepair() {
        return repair;
    }

    public void setRepair(boolean repair) {
        this.repair = repair;
    }

    public boolean isSetBottom() {
        return setBottom;
    }

    public void setSetBottom(boolean setBottom) {
        this.setBottom = setBottom;
    }

    public boolean isCageBottom() {
        return cageBottom;
    }

    public void setCageBottom(boolean cageBottom) {
        this.cageBottom = cageBottom;
    }

    public String getDoldarBar() {
        return doldarBar;
    }

    public void setDoldarBar(String doldarBar) {
        this.doldarBar = doldarBar;
    }

    public String getDoldarFoot() {
        return doldarFoot;
    }

    public void setDoldarFoot(String doldarFoot) {
        this.doldarFoot = doldarFoot;
    }

    public String getCustomerNote() {
        return customerNote;
    }

    public void setCustomerNote(String customerNote) {
        this.customerNote = customerNote;
    }

    public String getLabNote() {
        return labNote;
    }

    public void setLabNote(String labNote) {
        this.labNote = labNote;
    }

    public Worker getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(Worker assignedUser) {
        this.assignedUser = assignedUser;
    }

    public WorkStatus getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(WorkStatus workStatus) {
        this.workStatus = workStatus;
    }


    public BigDecimal calculatePrice() {
        BigDecimal basePrice = BigDecimal.ZERO;

        if(isMetal()){
            basePrice = basePrice.add(BigDecimal.valueOf(50));
        }

        if(isMetalAbove()){
            basePrice =  basePrice.add(BigDecimal.valueOf(50));
        }

        if(isBridge()){
            if (isZirkon()) {
                basePrice = basePrice.add(BigDecimal.valueOf(50 * 2)); // Zirkon köprü ücreti (x2)
            }
            if (isMetal()) {
                basePrice = basePrice.add(BigDecimal.valueOf(100 * 2)); // Metal köprü ücreti (x2)
            }
        }
        // Özelliklere göre fiyatı hesapla
        if (isTemp()) {
            basePrice = basePrice.add(BigDecimal.valueOf(100)); // Seçilen diş adeti kadar ücret
        }
        if (isZirkon()) {
            basePrice = basePrice.add(BigDecimal.valueOf(150)); // Zirkon ücreti (x2)
        }
        if (isEMax()) {
            basePrice = basePrice.add(BigDecimal.valueOf(150)); // E-Max ücreti
        }
        // Diğer özellikleri de burada hesaplayabilirsiniz

        // Gece plağı ücreti hesapla (alt ve üst ayrı fiyat x2)
        if (isNigthPlaque()) {
            basePrice = basePrice.add(BigDecimal.valueOf(250 * 2)); // Gece plağı ücreti (x2)
        }

        // Sert ve yumuşak ücretleri hesapla (ayrı fiyatlar)
        if (isHard()) {
            basePrice = basePrice.add(BigDecimal.valueOf(100)); // Sert ücreti ekle
        }
        if (!isHard()) {
            basePrice = basePrice.add(BigDecimal.valueOf(150)); // Yumuşak ücreti ekle
        }

        return basePrice;
    }

}
