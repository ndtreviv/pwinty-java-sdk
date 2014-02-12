package uk.co.mattburns.pwinty.v2;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import uk.co.mattburns.pwinty.v2.Photo.Sizing;

public class Order {

    private int id;
    private String recipientName;
    private String address1;
    private String address2;
    private String addressTownOrCity;
    private String stateOrCounty;
    private String postalOrZipCode;
    private CountryCode countryCode;
    private CountryCode destinationCountryCode;
    private int price;
    private Status status;
    private boolean useTrackedShipping;
    private ShippingInfo shippingInfo;
    private Payment payment;
    private URL paymentUrl;
    private QualityLevel qualityLevel;
    private List<Photo> photos = new ArrayList<Photo>();
    private String errorMessage;

    private Pwinty pwinty;

    public enum Status {
        NotYetSubmitted, Submitted, AwaitingPayment, Complete, Cancelled;
    }

    public enum Payment {
        InvoiceMe, InvoiceRecipient;
    }

    public enum QualityLevel {
        Pro, Standard;
    }

    // needed for GSON
    Order() {
    }

    public Order(Pwinty pwinty, CountryCode labCountry,
            CountryCode destinationCountry, QualityLevel quality) {
        this.pwinty = pwinty;
        this.countryCode = labCountry;
        this.destinationCountryCode = destinationCountry;
        this.qualityLevel = quality;
        Order order = pwinty.createOrder(this);
        overwriteThisOrderWithGivenOrder(order);
    }

    public int getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
        overwriteThisOrderWithGivenOrder(pwinty.updateOrder(id, this));
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
        overwriteThisOrderWithGivenOrder(pwinty.updateOrder(id, this));
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
        overwriteThisOrderWithGivenOrder(pwinty.updateOrder(id, this));
    }

    public String getAddressTownOrCity() {
        return addressTownOrCity;
    }

    public void setAddressTownOrCity(String addressTownOrCity) {
        this.addressTownOrCity = addressTownOrCity;
        overwriteThisOrderWithGivenOrder(pwinty.updateOrder(id, this));
    }

    public String getStateOrCounty() {
        return stateOrCounty;
    }

    public void setStateOrCounty(String stateOrCounty) {
        this.stateOrCounty = stateOrCounty;
        overwriteThisOrderWithGivenOrder(pwinty.updateOrder(id, this));
    }

    public String getPostalOrZipCode() {
        return postalOrZipCode;
    }

    public void setPostalOrZipCode(String postalOrZipCode) {
        this.postalOrZipCode = postalOrZipCode;
        overwriteThisOrderWithGivenOrder(pwinty.updateOrder(id, this));
    }

    public CountryCode getCountryCode() {
        return countryCode;
    }

    public CountryCode getDestinationCountryCode() {
        return destinationCountryCode;
    }

    public int getPrice() {
        return price;
    }

    public boolean isUseTrackedShipping() {
        return useTrackedShipping;
    }

    public void setUseTrackedShipping(boolean useTrackedShipping) {
        this.useTrackedShipping = useTrackedShipping;
        overwriteThisOrderWithGivenOrder(pwinty.updateOrder(id, this));
    }

    public ShippingInfo getShippingInfo() {
        return shippingInfo;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
        overwriteThisOrderWithGivenOrder(pwinty.updateOrder(id, this));
    }

    public URL getPaymentUrl() {
        return paymentUrl;
    }

    public QualityLevel getQualityLevel() {
        return qualityLevel;
    }

    public void setQualityLevel(QualityLevel qualityLevel) {
        this.qualityLevel = qualityLevel;
        overwriteThisOrderWithGivenOrder(pwinty.updateOrder(id, this));
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    private void refreshOrder() {
        overwriteThisOrderWithGivenOrder(pwinty.getOrder(id));
    }

    private void overwriteThisOrderWithGivenOrder(Order updated) {
        id = updated.id;
        status = updated.status;
        photos = updated.photos;
        recipientName = updated.recipientName;
        address1 = updated.address1;
        address2 = updated.address2;
        addressTownOrCity = updated.addressTownOrCity;
        stateOrCounty = updated.stateOrCounty;
        postalOrZipCode = updated.postalOrZipCode;
        useTrackedShipping = updated.useTrackedShipping;
        payment = updated.payment;
        qualityLevel = updated.qualityLevel;
        // Country codes are immutable, but just in case...
        if (countryCode != updated.countryCode
                || destinationCountryCode != updated.destinationCountryCode) {
            throw new RuntimeException("Cannot modify country code");
        }
    }

    public void cancel() {
        pwinty.cancelOrder(id);
        refreshOrder();
    }

    public SubmissionStatus getSubmissionStatus() {
        return pwinty.getSubmissionStatus(id);
    }

    /**
     * Add a photo File object to the order. This method will block until the
     * File is uploaded.
     */
    public Photo addPhoto(File photo, Photo.Type type, int copies, Sizing sizing) {
        Photo addedPhoto = pwinty.addPhotoToOrder(id, photo, type, copies,
                sizing);
        getPhotos().add(addedPhoto);
        return addedPhoto;
    }

    /**
     * Add a photo to the order using a public URL.
     */
    public Photo addPhoto(URL photoUrl, Photo.Type type, int copies,
            Sizing sizing) {
        Photo addedPhoto = pwinty.addPhotoToOrder(id, photoUrl, type, copies,
                sizing);
        getPhotos().add(addedPhoto);
        return addedPhoto;
    }

    public void deletePhoto(Photo photo) {
        pwinty.deletePhoto(getId(), photo.getId());
        overwriteThisOrderWithGivenOrder(pwinty.getOrder(id));
    }

    /**
     * Submit the Order for printing and shipping
     * 
     * If an error occurs, a {@link PwintyError} will be thrown
     */
    public void submit() {
        pwinty.submitOrder(id);
        overwriteThisOrderWithGivenOrder(pwinty.getOrder(id));
    }

    void setPwinty(Pwinty pwinty) {
        this.pwinty = pwinty;
    }
}
