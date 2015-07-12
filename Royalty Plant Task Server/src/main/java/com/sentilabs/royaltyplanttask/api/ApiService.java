package com.sentilabs.royaltyplanttask.api;

import com.sentilabs.royaltyplanttask.Constants;
import com.sentilabs.royaltyplanttask.converter.Converter;
import com.sentilabs.royaltyplanttask.entity.AccountEntity;
import com.sentilabs.royaltyplanttask.entity.DocumentEntity;
import com.sentilabs.royaltyplanttask.entity.UserEntity;
import com.sentilabs.royaltyplanttask.exception.BadParametersException;
import com.sentilabs.royaltyplanttask.exception.DuplicateException;
import com.sentilabs.royaltyplanttask.exception.NotFoundException;
import com.sentilabs.royaltyplanttask.request.BorrowMoneyRequest;
import com.sentilabs.royaltyplanttask.request.OpenAccountRequest;
import com.sentilabs.royaltyplanttask.request.TransferMoneyRequest;
import com.sentilabs.royaltyplanttask.response.AccountDetailResponse;
import com.sentilabs.royaltyplanttask.response.DocumentDetailResponse;
import com.sentilabs.royaltyplanttask.response.ErrorResponse;
import com.sentilabs.royaltyplanttask.service.interfaces.AccountService;
import com.sentilabs.royaltyplanttask.service.interfaces.BankUserService;
import com.sentilabs.royaltyplanttask.service.interfaces.DocumentService;
import com.sentilabs.royaltyplanttask.utils.UserUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by sentipy on 04/07/15.
 */

@RestController
@RequestMapping(value = "/api")
public class ApiService {

    private static Logger logger = LogManager.getLogger(ApiService.class);

    @Autowired
    private BankUserService bankUserService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private DocumentService documentService;

    @RequestMapping(value = "/registerUser", method = RequestMethod.POST)
    public ResponseEntity<UserEntity> registerUser(UserEntity userEntity) {
        logger.info("registering user " + userEntity.getUsername() + " through api");
        userEntity = bankUserService.registerUser(userEntity);
        return new ResponseEntity<>(userEntity, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public void loginUser(HttpServletRequest httpServletRequest) {
        if (httpServletRequest.getRemoteUser() != null) {
            return;
        }
        String userName = httpServletRequest.getParameter("username");
        String password = httpServletRequest.getParameter("password");
        try {
            httpServletRequest.login(userName, password);
            logger.info("logged in user = " + userName);
            final UserEntity userEntity = bankUserService.getUserByName(userName, true);
            httpServletRequest.getSession().setAttribute(Constants.SESSION_ATTR_NAME_WITH_CLIENT_ID
                    , userEntity.getId());
        } catch (ServletException e) {
            logger.error("Error logging in user " + userName, e);
            throw new NotFoundException("Invalid username/password", e);
        }
    }

    @RequestMapping(value = "/openAccount", method = RequestMethod.POST)
    public ResponseEntity<AccountDetailResponse> openAccount(OpenAccountRequest openAccountRequest
            , HttpServletRequest httpServletRequest) {
        long clientId = UserUtils.getCurrentUserId(httpServletRequest);
        openAccountRequest.setClientId(clientId);
        AccountEntity accountEntity = accountService.openAccount(openAccountRequest);
        AccountDetailResponse accountDetailResponse = Converter.convert(accountEntity);
        return new ResponseEntity<>(accountDetailResponse, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/getAccountsForCurrentClient", method = RequestMethod.POST)
    public ResponseEntity<List<AccountDetailResponse>> getAccounts(HttpServletRequest httpServletRequest) {
        long clientId = UserUtils.getCurrentUserId(httpServletRequest);
        List<AccountEntity> accountEntities = accountService.getAccountsForClientId(clientId);
        List<AccountDetailResponse> accountDetailResponses = Converter.convertAccountEntitiesListToAccountDetailResponses(accountEntities);
        return new ResponseEntity<>(accountDetailResponses, HttpStatus.OK);
    }

    @RequestMapping(value = "/borrow", method = RequestMethod.POST)
    public ResponseEntity<DocumentDetailResponse> borrow(BorrowMoneyRequest borrowMoneyRequest
            , HttpServletRequest httpServletRequest) {
        long clientId = UserUtils.getCurrentUserId(httpServletRequest);
        //TODO: check that account really belongs to client
        DocumentEntity documentEntity = documentService.borrow(borrowMoneyRequest, clientId);
        DocumentDetailResponse documentDetailResponse = Converter.convert(documentEntity);
        return new ResponseEntity<>(documentDetailResponse, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/borrow", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DocumentDetailResponse> borrowWithJSON(@RequestBody BorrowMoneyRequest borrowMoneyRequest
            , HttpServletRequest httpServletRequest) {
        long clientId = UserUtils.getCurrentUserId(httpServletRequest);
        //TODO: check that account really belongs to client
        DocumentEntity documentEntity = documentService.borrow(borrowMoneyRequest, clientId);
        DocumentDetailResponse documentDetailResponse = Converter.convert(documentEntity);
        return new ResponseEntity<>(documentDetailResponse, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/transfer", method = RequestMethod.POST)
    public ResponseEntity<DocumentDetailResponse> transfer(TransferMoneyRequest transferMoneyRequest
            , HttpServletRequest httpServletRequest) {
        long clientId = UserUtils.getCurrentUserId(httpServletRequest);
        //TODO: check that account really belongs to client
        DocumentEntity documentEntity = documentService.transfer(transferMoneyRequest, clientId);
        DocumentDetailResponse documentDetailResponse = Converter.convert(documentEntity);
        return new ResponseEntity<>(documentDetailResponse, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/getDocumentsForAccountNumber", method = RequestMethod.POST)
    public ResponseEntity<List<DocumentDetailResponse>> getDocumentsForAccount(HttpServletRequest httpServletRequest) {
        long clientId = UserUtils.getCurrentUserId(httpServletRequest);
        //TODO: check that account really belongs to client
        String accountNumber = httpServletRequest.getParameter("accountNumber");
        if (StringUtils.isEmpty(accountNumber)) {
            throw new BadParametersException("account number not set");
        }
        final List<DocumentEntity> documentEntities = documentService.getDocumentsForAccountNumber(accountNumber);
        final List<DocumentDetailResponse> documentDetailResponses = Converter.convertDocumentEntitiesToDocumentDetailResponses(documentEntities);
        return new ResponseEntity<>(documentDetailResponses, HttpStatus.OK);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex) {
        logger.error(ex);
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorText(ex.getMessage());
        return new ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateException(DuplicateException ex) {
        logger.error(ex);
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorText(ex.getMessage());
        return new ResponseEntity(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({Exception.class, RuntimeException.class})
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        logger.error(ex);
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorText("Unknown error occurred. Please contact HelpDesk.");
        return new ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST);
    }




}
