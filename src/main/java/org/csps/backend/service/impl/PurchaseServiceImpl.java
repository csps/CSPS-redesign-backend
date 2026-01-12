// package org.csps.backend.service.impl;

// import java.math.BigDecimal;
// import java.math.RoundingMode;
// import java.time.LocalDateTime;
// import java.util.ArrayList;
// import java.util.List;

// import org.csps.backend.domain.dtos.request.PurchaseItemRequestDTO;
// import org.csps.backend.domain.dtos.request.PurchaseRequestDTO;
// import org.csps.backend.domain.dtos.response.PurchaseResponseDTO;
// import org.csps.backend.domain.entities.MerchVariant;
// import org.csps.backend.domain.entities.Purchase;
// import org.csps.backend.domain.entities.PurchaseItem;
// import org.csps.backend.domain.entities.Student;
// import org.csps.backend.domain.entities.composites.PurchaseItemId;
// import org.csps.backend.domain.enums.PurchaseItemStatus;
// import org.csps.backend.exception.InsufficientBalanceException;
// import org.csps.backend.exception.MerchVariantNotFoundException;
// import org.csps.backend.exception.OutOfStockException;
// import org.csps.backend.exception.PurchaseNotFoundException;
// import org.csps.backend.exception.StudentNotFoundException;
// import org.csps.backend.mapper.PurchaseMapper;
// import org.csps.backend.repository.MerchVariantRepository;
// import org.csps.backend.repository.PurchaseRepository;
// import org.csps.backend.repository.StudentRepository;
// import org.csps.backend.service.PurchaseService;
// import org.springframework.stereotype.Service;

// import jakarta.transaction.Transactional;
// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class PurchaseServiceImpl implements PurchaseService{

//     // Repositories
//     private final PurchaseRepository purchaseRepository;
//     private final StudentRepository studentRepository;
//     private final MerchVariantRepository merchVariantRepository;

//     // Mapper
//     private final PurchaseMapper purchaseMapper;
    
    
//     @Override
//     @Transactional
//     public PurchaseResponseDTO createPurchase(PurchaseRequestDTO purchaseRequestDTO) {
//         // Get student ID and money
//         String studentId = purchaseRequestDTO.getStudentId();
//         BigDecimal money = BigDecimal.valueOf(purchaseRequestDTO.getReceivedMoney());
        
        
//         // Find student
//         Student student = studentRepository.findByStudentId(studentId)
//                 .orElseThrow(() -> new StudentNotFoundException("Student not found"));


//         // Create purchase
//         Purchase purchase = new Purchase();
//         purchase.setStudent(student);
//         purchase.setPurchasedAt(LocalDateTime.now());

//         // Instantiating for total price
//         BigDecimal totalPrice = BigDecimal.ZERO;
        
//         // Traversing all the items
//         for (PurchaseItemRequestDTO itemRequest :  purchaseRequestDTO.getItems()) {
//             // Find merch variant
//             MerchVariant merchVariant = merchVariantRepository.findById(itemRequest.getMerchVariantId())
//                         .orElseThrow(() -> new MerchVariantNotFoundException("Merch Variant not found"));

//             // Calculate price
//             BigDecimal price = BigDecimal.valueOf(merchVariant.getPrice())
//                         .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

//             // Add to total price
//             totalPrice = totalPrice.add(price);
            
//         }

//         // Check if money is sufficient
//         if (money.compareTo(totalPrice) < 0) {
//             throw new InsufficientBalanceException("Invalid Money");
//         }

//         // Calculate change
//         BigDecimal change = money.subtract(totalPrice).setScale(2, RoundingMode.HALF_UP);

//         List<PurchaseItem> purchaseItems = new ArrayList<>();

//         // Traversing all the items
//         for (PurchaseItemRequestDTO itemRequest : purchaseRequestDTO.getItems()) {
//             MerchVariant merchVariant = merchVariantRepository.findById(itemRequest.getMerchVariantId())
//                     .orElseThrow(() -> new MerchVariantNotFoundException("Merch Variant not found"));

//             int stock = merchVariant.getStockQuantity();
//             Long merchVariantId = merchVariant.getMerchVariantId();
//             Long purchaseId = purchase.getPurchaseId();
//             int purchaseQuantity = itemRequest.getQuantity();
//             if (purchaseQuantity > stock) {
//                 throw new OutOfStockException("Out of stock for merchVariantId " + itemRequest.getMerchVariantId());
//             }
                
                
//                 PurchaseItem purchaseItem = new PurchaseItem();
//                 purchaseItem.setPurchase(purchase);
//                 purchaseItem.setPurchaseItemId(new PurchaseItemId(purchaseId,merchVariantId));
//                 purchaseItem.setMerchVariant(merchVariant);
//                 purchaseItem.setQuantity(itemRequest.getQuantity());
//                 purchaseItem.setStatus(PurchaseItemStatus.NOT_PAID);
    
//                 purchaseItems.add(purchaseItem);
    
//                 int currentStockQuantity = merchVariant.getStockQuantity();
//                 merchVariant.setStockQuantity(currentStockQuantity - itemRequest.getQuantity());            
//         }

//         // Set purchase items
//         purchase.setItems(purchaseItems);
//         // Set total price
//         purchase.setTotalPrice(totalPrice.doubleValue());   
//         purchase.setReceivedMoney(purchaseRequestDTO.getReceivedMoney());
//         purchase.setChange(change.doubleValue()); 

//         Purchase savedPurchase = purchaseRepository.save(purchase);

//         PurchaseResponseDTO response = purchaseMapper.toResponseDTO(savedPurchase);
//         return response;
//     }


//     @Override
//     public List<PurchaseResponseDTO> getPurchaseByStudentId(String studentId) {
//         List<Purchase> purchases = purchaseRepository.findByStudentStudentId(studentId);

//         return purchases.stream()
//                         .map(purchaseMapper::toResponseDTO)
//                         .toList();
//     }


//     @Override
//     public List<PurchaseResponseDTO> getAllPurchases() {
//         List<Purchase> purchases = purchaseRepository.findAll();

//         List<PurchaseResponseDTO> purchaseResponse = purchases.stream()
//                                                      .map(purchaseMapper::toResponseDTO)
//                                                      .toList();

//         return purchaseResponse;
//     }


// }
