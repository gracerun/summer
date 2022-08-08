<template>
	<div class="admin-page">
		<div class="admin-main-box">
			<!-- search form start -->
			<myp-search-form @changeform="callbackformHandle" @resetInput="resetSearchHandle" @visiblesome="visiblesomeHandle" @changeSearchVisible="changeSearchVisible" @SearchInit="searchInit" @seachstart="seachstartHandle" :searchOptions="searchOptions" :searchLableWidth="searchOptions.lableWidth" :searchList="searchOptions.searchList"></myp-search-form>
			<!-- search form end -->
			<div class="operation-box">
				<el-button-group class="button-group">
					<el-button v-if="adminFilter('/${requestMapping}/create')" class="mybutton" @click="showAddForm" size="small" type="primary" icon="el-icon-plus">新增</el-button>
				</el-button-group>
			</div>
			<myp-data-page :actionUrl="actionUrl" @pagecount="pagecountHandle" @pagelimit="pagelimitHandle" @operation="operationHandle" ref="dataTable" :tableDataInit="tableData" :dataList="dataList" :page="postPage" :limit="postLimit" :search="searchCondition" :dataCount="dataCount" :loading="tableLading"></myp-data-page>
		</div>
		<!-- 新增或修改start -->
		<myp-dialog top width="800px" :title="(isAdd?'新增':'修改')+'${entityDesc}'" :visible.sync="mypFormVisible">
			<div slot="content">
				<myp-dynamic-form ref="form" :config="mypFormConfig" :data="mypFormData" :rules="mypFormRules"></myp-dynamic-form>
			</div>
			<div slot="footer">
				<el-button icon="el-icon-circle-close" size="small" type="default" @click="resetForm">取消</el-button>
				<el-button :loading="saveLoading" icon="el-icon-circle-check" size="small" type="primary" @click="saveForm">提交</el-button>
			</div>
		</myp-dialog>
		<!-- 新增或修改end -->
		<!-- 详情 begin -->
		<myp-dialog :visible="detailVisible" @close="detailVisible = false" title="查看详情" append-to-body width="730px">
			<div slot="content">
				<myp-detail :config="detailConfig" :data="detailData" group="${requestMapping}"></myp-detail>
			</div>
			<div slot="footer">
				<el-button icon="el-icon-circle-close" size="small" type="default" @click="detailVisible = false">关闭</el-button>
			</div>
		</myp-dialog>
		<!-- 详情 end -->
	</div>
</template>

<script>
	import SearchForm from "@src/components/SearchForm";
	import DataPage from "@src/components/DataPage";
	import { mixinsPc } from "@src/common/mixinsPc";
	import { mixinDataTable } from "@src/components/DataPage/dataPage";
	import { Today } from "@src/common/dateSerialize";
	import MypDialog from "@src/components/MypDialog";
	import MypDetail from "@/components/MypDetail";
	import MypDynamicForm from "@src/components/MypDynamicForm";
	import detailConfig from "./${requestMapping}Detail.json";
	import ${requestMapping}Form from "./${requestMapping}Form.json";

	import {
		_${requestMapping}_findByPage,
		_${requestMapping}_findById,
		_${requestMapping}_create,
		_${requestMapping}_update,
		_${requestMapping}_exportExcel,
		_${requestMapping}_exportExcelTmp
	} from "@src/apis/${requestMapping}_api";

	export default {
		name: "${requestMapping}",
		components: {
			"myp-search-form": SearchForm, // 搜索组件
			"myp-data-page": DataPage, // 数据列表组件
			"myp-dialog": MypDialog, // 子层弹出框
			"myp-detail": MypDetail, // 详情组件
			"myp-dynamic-form": MypDynamicForm, // 表单组件
		},
		mixins: [mixinDataTable, mixinsPc],
		data() {
			const searchObject = () => ({
				${searchElements}
				pageNum: 1,
				pageSize: 10
			});
			return {
				actionUrl: _${requestMapping}_findByPage,
				dataList: [],
				productList: [],
				formLabelWidth: "120px",
				mypFormVisible: false,
				mypFormData: ${requestMapping}Form.data,
				mypFormConfig: ${requestMapping}Form.config,
				mypFormRules: ${requestMapping}Form.rules,
				isAdd: true, // 是新增还是修改
				// 顶部搜索表单信息
				// 查询条件数据
				searchConditionDefault: searchObject(),
				searchCondition: searchObject(),
				searchOptions: { lableWidth: "100px", searchList: [] },
				// 详情
				detailData: {},
				detailVisible: false,
				detailConfig: detailConfig,
				// 列表数据
				tableData: {
					dataHeader: [
						// table列信息 key=>表头标题，word=>表内容信息
						${tables}
					],
					operation: {
						// 操作按钮
						width: "150px",
						options: [
							{
								color: "#67C23A",
								text: "编辑",
								visibleFn: () => {
									return this.adminFilter("/${requestMapping}/update");
								},
								cb: rowdata => {
									this.editForm = Object.assign({}, rowdata);
									this.editFormVisible = true;
								}
							}
						]
					},
					// 数据加载成功
					dataSuccess: data => {
						// console.log("数据加载完成");
					}
				}
			};
		},
		methods: {
			searchInit() {
				this.searchOptions = {
					lableWidth: this.formLabelWidth,
					searchList: [
						// 请注意 该数组里对象的corresattr属性值与searchCondition里面的属性是一一对应的 不可少
						${searchElementInputs}
					]
				};
			},
			showAddForm() {
				this.mypFormVisible = this.isAdd = true;
			},
			showEditForm(id) {
				_${requestMapping}_findById(id)().then(result => {
					this.isAdd = false;
					this.mypFormData = result.data;
					this.mypFormVisible = true;
				});
			},
			// 显示详情
			showDetail(row) {
				let data = JSON.parse(JSON.stringify(row));
				this.detailData = {};
				_${requestMapping}_findById(data.id)().then(result => {
					let data = result.data;
					// TODO 翻译字典
					this.detailVisible = true;
				});
			},
			resetForm() {
				this.$refs.form.getForm().resetFields();
				this.mypFormVisible = false;
			},
			saveForm() {
				// 新增内容保存
				let form = this.$refs.form.getForm();
				form.validate(valid => {
					if (valid) {
						this.saveLoading = true;
						let opt = this.isAdd ? _${requestMapping}_create(): _${requestMapping}_update();
						opt(this.mypFormData).then(data => {
							this.Message_pc({
								message: '恭喜您，${entityDesc}' + (this.isAdd ? '新增' : '修改') + '成功',
								type: "success",
								center: true
							});
							this.resetForm();
							this.seachstartHandle();
							this.saveLoading = false;
						}).catch(err => {
							this.saveLoading = false;
						});;
					}
				})
			}
		},
		watch: {
			mypFormVisible(val) {
				this.saveLoadingStop(val);
			}
		},
		computed: { },
		mounted() { },
		created() {
			this.searchInit();
		}
	};
</script>
<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="scss" scoped>
</style>
