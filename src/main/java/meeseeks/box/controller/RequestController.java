package meeseeks.box.controller;

import meeseeks.box.domain.RequestEntity;
import meeseeks.box.exception.NotFoundException;
import meeseeks.box.repository.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/request")
public class RequestController {

    private final RequestRepository requestRepository;

    @Autowired
    public RequestController(final RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    // TODO Test
    @ResponseBody
    @RequestMapping("/insert")
    public RequestEntity insert(@Valid @RequestBody RequestEntity request) {
        return requestRepository.save(request);
    }

    // TODO Test
    @ResponseBody
    @RequestMapping("/delete/{id}")
    public ResponseEntity delete(@PathVariable("id") Integer id) {
        requestRepository.delete(findRequestById(id));
        return new ResponseEntity(HttpStatus.OK);
    }

    // TODO Test
    @ResponseBody
    @RequestMapping("/update")
    public RequestEntity update(@Valid @RequestBody RequestEntity updated) {
        return requestRepository.save(updated);
    }

    // TODO Test
    @ResponseBody
    @RequestMapping("/get/{id}")
    public RequestEntity getRequestById(@PathVariable("id") Integer id) {
        return findRequestById(id);
    }

    // TODO Test
    @ResponseBody
    @RequestMapping("/find/between/{limit}")
    public List<RequestEntity> insert(@RequestBody DateRange range,
                                      @PathVariable("limit") Integer limit) {
        return requestRepository.findByDateBetween(range.getStart(), range.getEnd(), new PageRequest(0, limit));
    }

    // TODO: Test
    @ResponseBody
    @RequestMapping("/latest/job/{idJob}/{limit}")
    public List<RequestEntity> getLatestRequestsFromJob(@PathVariable("idJob") Integer id,
                                                        @PathVariable("limit") Integer limit) {
        return requestRepository.findLatestRequestsFromJob(id, new PageRequest(0, limit));
    }

    // TODO: Test
    @ResponseBody
    @RequestMapping("/latest/provider/{idProvider}/{limit}")
    public List<RequestEntity> getLatestRequestsForProvider(@PathVariable("idProvider") Integer id,
                                                            @PathVariable("limit") Integer limit) {
        return requestRepository.findLatestRequestsForProvider(id, new PageRequest(0, limit));
    }

    // TODO Test
    @ResponseBody
    @RequestMapping("/latest/provider/accepted/{idProvider}/{limit}")
    public List<RequestEntity> getLatestAcceptedRequestsForProvider(@PathVariable("idProvider") Integer id,
                                                              @PathVariable("limit") Integer limit) {
        return requestRepository.findLatestAcceptedRequestsForProvider(id, new PageRequest(0, limit));
    }

    private RequestEntity findRequestById(@PathVariable("id") Integer id) {
        return requestRepository.findById(id).orElseThrow(() -> new NotFoundException("Request Not Found!"));
    }
}
