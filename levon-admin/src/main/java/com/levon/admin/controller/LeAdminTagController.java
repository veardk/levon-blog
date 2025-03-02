package com.levon.admin.controller;

import com.levon.framework.common.annotation.SystemLog;
import com.levon.framework.common.response.ResponseResult;
import com.levon.framework.domain.dto.AdminTagCreateValidationDTO;
import com.levon.framework.domain.dto.AdminTagUpdateValidationDTO;
import com.levon.framework.service.LeBlogTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/content/tag")
public class LeAdminTagController {

    @Autowired
    private LeBlogTagService leBlogTagService;

    /**
     * 获取所有标签
     *
     */
    @GetMapping("/listAllTag")
    public ResponseResult getAllTag(){
        return ResponseResult.okResult(leBlogTagService.getAllTag());
    }

    /**
     * 获取标签列表
     * <p>
     * 根据分页参数和过滤条件获取标签列表。允许按名称和备注进行可选过滤。
     * 默认分页参数为第一页，每页10条记录。
     *
     * @param pageNum  当前页码，默认为1
     * @param pageSize 每页显示的记录数，默认为10
     * @param name     可选过滤参数，根据标签名称过滤
     * @param remark   可选过滤参数，根据备注信息过滤
     * @return ResponseResult 包含符合条件的标签列表
     */
    @GetMapping("/list")
    @SystemLog("获取标签列表")
    public ResponseResult getTagList(@RequestParam(defaultValue = "1") Integer pageNum,
                                     @RequestParam(defaultValue = "10") Integer pageSize,
                                     @RequestParam(required = false) String name,
                                     @RequestParam(required = false) String remark) {
        return ResponseResult.okResult(leBlogTagService.getTagList(pageNum, pageSize, name, remark));
    }

    /**
     * 查询标签的详细信息
     *
     * @param id 标签的唯一标识符
     * @return 返回包含标签详细信息的响应结果
     */
    @GetMapping("{id}")
    @SystemLog("查询标签详细")
    public ResponseResult getTagDetail(@NotNull(message = "id不能为空")@PathVariable Long id) {
        return ResponseResult.okResult(leBlogTagService.getTagDetail(id));
    }

    /**
     * 新增标签
     *
     * 该方法接收一个标签创建请求，调用服务层方法进行标签的创建，并返回操作结果。
     *
     * @param createRequest 标签创建请求对象，包含标签的相关信息
     * @return 返回操作结果，包含创建成功的响应
     */
    @PostMapping
    @SystemLog("新增标签")
    public ResponseResult createTag(@Validated @RequestBody AdminTagCreateValidationDTO createRequest) {
        leBlogTagService.createTag(createRequest);
        return ResponseResult.okResult();
    }

    /**
     * 修改标签
     *
     * 该方法接收一个标签更新请求，调用服务层方法进行标签的更新，并返回操作结果。
     *
     * @param updateRequest 标签更新请求对象，包含标签的相关信息
     * @return 返回操作结果，包含更新成功的响应
     */
    @PutMapping
    @SystemLog("修改标签")
    public ResponseResult updateTag(@Validated @RequestBody AdminTagUpdateValidationDTO updateRequest) {
        leBlogTagService.updateTag(updateRequest);
        return ResponseResult.okResult();
    }

    /**
     * 删除标签
     *
     * 该方法接收一个包含多个标签 ID 的字符串，解析后调用服务层方法删除对应的标签。
     *
     * @param tagIds 以逗号分隔的标签 ID 列表
     * @return 返回操作结果，包含删除成功的响应
     */
    @DeleteMapping("/{tagIds}")
    @SystemLog("删除标签")
    public ResponseResult deleteTags(@NotNull(message = "ids不能为空") @PathVariable("tagIds")  String tagIds) {
        List<Long> ids = Arrays.stream(tagIds.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());

        if (ids.size() == 1) {
            leBlogTagService.deleteTag(ids.get(0));
        } else {
            leBlogTagService.deleteTags(ids);
        }
        return ResponseResult.okResult();
    }

}
