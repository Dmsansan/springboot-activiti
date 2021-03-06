package org.hswebframework.web.workflow.flowable.modeler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@RestController
@RequestMapping("/workflow")
public class FlowableModelController {

    @Autowired
    private RepositoryService repositoryService;

    private final static String MODEL_ID          = "modelId";
    private final static String MODEL_NAME        = "name";
    private final static String MODEL_REVISION    = "revision";
    private final static String MODEL_DESCRIPTION = "description";
    private final static String MODEL_KEY         = "key";

    @RequestMapping(value = "/model/{modelId}/json", method = RequestMethod.GET)
    public Object getEditorJson(@PathVariable String modelId) throws Exception {
        JSONObject modelNode;
        Model model = repositoryService.getModel(modelId);
        if (model == null) throw new NullPointerException("模型不存在");
        if (StringUtils.isNotEmpty(model.getMetaInfo())) {
            modelNode = JSON.parseObject(model.getMetaInfo());
        } else {
            modelNode = new JSONObject();
            modelNode.put(MODEL_NAME, model.getName());
        }
        modelNode.put(MODEL_ID, model.getId());
        modelNode.put("model", JSON.parse(new String(repositoryService.getModelEditorSource(model.getId()))));
        return modelNode;
    }

    @RequestMapping(value = "/model/{modelId}", method = RequestMethod.PUT)
    @ResponseStatus(value = HttpStatus.OK)
    public void saveModel(@PathVariable String modelId,
                          @RequestParam Map<String, String> values) throws TranscoderException, IOException {
        Model model = repositoryService.getModel(modelId);
        JSONObject modelJson = JSON.parseObject(model.getMetaInfo());

        modelJson.put(MODEL_NAME, values.get("name"));
        modelJson.put(MODEL_DESCRIPTION, values.get("description"));

        model.setMetaInfo(modelJson.toString());
        model.setName(values.get("name"));

        repositoryService.saveModel(model);

        repositoryService.addModelEditorSource(model.getId(), values.get("json_xml").getBytes("utf-8"));

        InputStream svgStream = new ByteArrayInputStream(values.get("svg_xml").getBytes("utf-8"));
        TranscoderInput input = new TranscoderInput(svgStream);

        PNGTranscoder transcoder = new PNGTranscoder();
        // Setup output
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        TranscoderOutput output = new TranscoderOutput(outStream);

        // Do the transformation
        transcoder.transcode(input, output);
        final byte[] result = outStream.toByteArray();
        repositoryService.addModelEditorSourceExtra(model.getId(), result);
        outStream.close();
    }

}