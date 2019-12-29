/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sec.project.domain;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
public class Bank extends AbstractPersistable<Long> {

    private String name;
    private String number;
//    private String contentType;
//    private Long contentLength;

    @ManyToOne
    private User user;

//    @Lob
//    @Basic(fetch = FetchType.LAZY)
//    private byte[] content;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    public String getNumber() {
        return number;
    }

    public void setNumber(String newNubber) {
        this.number = newNubber;
    }

//    public String getContentType() {
//        return contentType;
//    }
//
//    public void setContentType(String contentType) {
//        this.contentType = contentType;
//    }
//
//    public Long getContentLength() {
//        return contentLength;
//    }
//
//    public void setContentLength(Long contentLength) {
//        this.contentLength = contentLength;
//    }

    

//    public byte[] getContent() {
//        return content;
//    }
//
//    public void setContent(byte[] content) {
//        this.content = content;
//    }

}
